from django.shortcuts import render
from rest_framework import  status
from .serializers import UserSerializer
from .models import  User
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.exceptions import AuthenticationFailed
import jwt, datetime
import requests
from requests.auth import HTTPBasicAuth
import base64
from datetime import datetime as dt
from django.contrib.auth import authenticate
from concurrent.futures import ThreadPoolExecutor
from django.core.mail import send_mail, EmailMessage
from GitMD.settings import EMAIL_HOST_USER
import random
import string
from django.core.mail import EmailMultiAlternatives
from django.template.loader import render_to_string
from django.utils.html import strip_tags

# Create your views here. 

BASE_URL = "http://gitea.gitmd.ie:80/api/v1"

class MarkdownFileView(APIView):

    def post(self, request, format=None):

        jwtToken = request.COOKIES.get('jwt')
        switch = request.data["switch"]
        repoName = request.data["repoName"]
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        name = user.name
        if(switch == "repo"):
            url = f"{BASE_URL}/user/repos"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {
            }
            response = requests.get(url, json=data, headers=headers)
            listOwner = [item for item in response.json() if item['owner']['login'] == name]
            listShared = [item for item in response.json() if item['owner']['login'] != name]
            data_to_return = [
                response.json(), listOwner, listShared
            ]
            return Response(data_to_return)

        elif(switch == "files"):
            url = f"{BASE_URL}/repos/" + name + "/" + repoName + "/contents"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {
            }
            response = requests.get(url, json=data, headers=headers)

            if(response.status_code != 200):
                name = GiteaAPIUtils.make_owner_search_request(repoName, token)
                url = f"{BASE_URL}/repos/" + name + "/" + repoName + "/contents"
                headers = {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                }
                data = {
                }
                response = requests.get(url, json=data, headers=headers)

            dates = []
            with ThreadPoolExecutor(max_workers=5) as executor:
                futures = []
                for data in response.json():
                    future = executor.submit(self.get_files, data, name, repoName, token)
                    futures.append(future)

                for future in futures:
                    result = future.result()
                    if result is not None:
                        dates.append(result)
            
            return Response(dates)
        
        elif(switch == "deletedFiles"):
            name = GiteaAPIUtils.make_owner_search_request(repoName, token)
            url = f"{BASE_URL}/repos/{name}/{repoName}/commits"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {}
            response = requests.get(url, json=data, headers=headers)

            removed_files_with_date = []
            removed_files = set()
            with ThreadPoolExecutor(max_workers=5) as executor:
                futures = []
                for data in response.json():
                    future = executor.submit(self.get_deleted_files, data, [removed_files])
                    futures.append(future)

                for future in futures:
                    result = future.result()
                    if result is not None:
                        removed_files_with_date.append(result)
                        removed_files.add(result[0])

            print(removed_files_with_date)
            return Response(removed_files_with_date)
        
    def get_files(self, data, name, repoName, token):
        headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        url = f"{BASE_URL}/repos/" + name + "/" + repoName + "/commits?path=" + data["name"]
        response2 = requests.get(url, headers=headers)
        return [data["name"], response2.json()[-1]["created"]]
    
    def get_deleted_files(self, data, removed_files):
        print(data)
        date = data["created"]
        for file_data in data["files"]:
            if file_data["status"] == "removed" and file_data["filename"] not in removed_files:
                return [file_data["filename"], date]


class MarkdownFileCreate(APIView):

    def post(self, request, format=None):

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        username = user.name

        repoTitle = request.data.get('repoTitle')
        title = request.data.get('title')
        content = request.data.get('content')
        encoded_bytes = base64.b64encode(content.encode('utf-8'))
        content = encoded_bytes.decode('utf-8')

        try:
            url = f"{BASE_URL}/user/repos"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {
                'name' : repoTitle,
                'private': True
            }
            response = requests.post(url, json=data, headers=headers)
            if response.status_code != 201: 
                return Response({'error': 'Failed to create user repo'}, status=response.status_code)

        finally:
            filename = title + ".md"
            url = f"{BASE_URL}/repos/" + username + "/" + repoTitle + "/contents/" + filename
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'token ' + token
            }
            data = {
                'content' : content
            }
            response = requests.post(url, json=data, headers=headers)
            if response.status_code == 201:
                return Response(response.json(), status=status.HTTP_201_CREATED)
            else:
                return Response({'error': 'Failed to create user file'}, status=response.status_code)
            
        
class MarkdownFileEdit(APIView):

    def put(self, request, format=None):

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        user = request.data.get('user')
        file = request.data.get('file')
        repo = request.data.get('repo')
        sha = request.data.get('sha')
        content = request.data.get('content')
        encoded_bytes = base64.b64encode(content.encode('utf-8'))
        content = encoded_bytes.decode('utf-8')
        url = f"{BASE_URL}/repos/" + user + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'content' : content,
            'sha' : sha,
        }
        response = requests.put(url, json=data, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to update user file'}, status=response.status_code)

        return Response(response.json(), status=status.HTTP_200_OK)
    
class RestoreDeletedFile(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        repo = request.data.get('repoName')
        file = request.data.get('file')
        owner = GiteaAPIUtils.make_owner_search_request(repo, token)
        url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/commits"
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to get previous commits'}, status=response.status_code)
        
        i=0
        for commit_data in response.json():
            if(i == 1):
                break
            else:
                try:
                    if(commit_data["files"][0]["filename"] == file):
                        if(commit_data["files"][0]["status"] != "removed"):
                            sha = commit_data["sha"]
                            i = 1

                except:
                    print("No change entry")

        url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/contents/" + file + "?ref=" + sha
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to get previous commits content'}, status=response.status_code)
        content = response.json()["content"]

        url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'content' : content,
            'sha' : sha
        }
        response = requests.post(url, json=data, headers=headers)
        new_commit_sha = response.json()['content']['sha']
        requests.patch(f"{BASE_URL}/repos/{owner}/{repo}/git/refs/heads/master",
            json={'sha': new_commit_sha}, headers=headers)

        return Response(status=status.HTTP_200_OK)
    
class GetPreviousVersions(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        repo = request.data.get('repo')
        file = request.data.get('file')
        owner = request.data.get('owner')
        url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/commits"
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to get previous commits'}, status=response.status_code)
        Commitlist = []
        for commit_data in response.json():
            if(len(Commitlist) > 2):
                break
            else:
                try:
                    if(commit_data["files"][0]["filename"] == file):
                        if(commit_data["files"][0]["status"] == "removed"):
                            break
                        sha = commit_data["sha"]
                        author_name = commit_data["commit"]["author"]["name"]
                        author_date = commit_data["commit"]["author"]["date"]
                        if("+" in author_date):
                            author_date = author_date.split("+")[0]
                        date_split = dt.strptime(author_date, "%Y-%m-%dT%H:%M:%S")
                        formatted_date = date_split.strftime("%d/%m/%Y")
                        formatted_time = date_split.strftime("%H:%M:%S")

                        Commitlist.append([sha, author_name, formatted_date, formatted_time])
                except:
                    print("No change entry")
        for commit in Commitlist:
            url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/contents/" + file + "?ref=" + commit[0]
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            response = requests.get(url, headers=headers)
            if response.status_code != 200: 
                return Response({'error': 'Failed to get previous commits content'}, status=response.status_code)
            commit[0] = response.json()["content"]

        return Response(Commitlist, status=status.HTTP_200_OK)



class MarkdownFileDelete(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        file = request.data.get('file')
        repo = request.data.get('repo')
        user = GiteaAPIUtils.make_owner_search_request(repo, token)

        url = f"{BASE_URL}/repos/" + user + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        sha = response.json()["sha"]

        url = f"{BASE_URL}/repos/" + user + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'sha' : sha,
        }
        response = requests.delete(url, json=data, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to delete user file'}, status=response.status_code)

        return Response(response.json(), status=status.HTTP_200_OK)
    
class RepoDelete(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        username = user.name
        token = user.token
        repo = request.data.get('repo')
        url = f"{BASE_URL}/repos/" + username + "/" + repo
        headers = {
            'Authorization': 'Bearer ' + token
        }

        response = requests.delete(url, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to delete user repo'}, status=response.status_code)

        return Response(response.json(), status=status.HTTP_200_OK)

class MarkdownFileDetails(APIView):

    def get(self, request, username, repo, file, format=None):

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        url = f"{BASE_URL}/repos/" + username + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            return Response(response.json(), status=status.HTTP_200_OK)
        else:
            return Response({'error': 'Failed to get file content'}, status=response.status_code)
        
class AddUserToRepo(APIView):

    def put(self, request, format=None):

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        owner = user.name
        repo = request.data.get('repo')
        addedUser = request.data.get('addedUser')
        repoFullName = request.data.get('repoFullName')
        
        if(addedUser == GiteaAPIUtils.make_owner_search_request(repoFullName, token)):
            return Response({'error': 'Collaborator is already owner'})

        url = f"{BASE_URL}/repos/" + owner + "/" + repo + "/collaborators/" + addedUser
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'permissions': "admin"
        }
        response = requests.put(url, json=data, headers=headers)
        if response.status_code == 204:
            return Response(status=status.HTTP_204_NO_CONTENT)
        else:
            return Response({'error': 'Failed to add collaborator'}, status=response.status_code)
        
class GetCollaborators(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        repoName = request.data.get('repoName')
        repoFullName = request.data.get('repoFullName')
        owner = GiteaAPIUtils.make_owner_search_request(repoFullName, token)
        url = f"{BASE_URL}/repos/" + owner + "/" + repoName + "/collaborators"
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code != 200: 
            return Response({'error': 'Failed to get collaborators'}, status=response.status_code)

        collaborators = [user["login"] for user in response.json()]
        collaborators.insert(0, owner)
        return Response(collaborators, status=status.HTTP_200_OK)
    
class RemoveCollaborator(APIView):

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        repoName = request.data.get('repoName')
        repoFullName = request.data.get('repoFullName')
        collaborator = request.data.get('collaborator')
        owner = GiteaAPIUtils.make_owner_search_request(repoFullName, token)
        url = f"{BASE_URL}/repos/" + owner + "/" + repoName + "/collaborators/" + collaborator
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.delete(url, headers=headers)
        if response.status_code != 204: 
            return Response({'error': 'Failed to delete collaborator'}, status=response.status_code)

        return Response(status=status.HTTP_200_OK)

class Register(APIView):

    def post(self, request):
        name = request.data['name']
        email = request.data['email']
        password = request.data['password']

        url = f"{BASE_URL}/admin/users"

        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'token 9812c3c5008c1da5927f7ef20b45535116a8ee87'
        }

        data = {
            'username': name,
            'password': password,
            'email': email,
            "must_change_password": False
        }
        response = requests.post(url, json=data, headers=headers)
        if response.status_code != 201:
            return Response({'error': 'Failed to create user'}, status=response.status_code)
        serializer = UserSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        
        url = f"{BASE_URL}/users/" + name + "/tokens"

        headers = {
            'Content-Type': 'application/json',
        }

        data = {
            'name': 'auth_key',
            'scopes': ['write:issue', 'write:notification', 'write:repository', 'write:user']
        }
        response = requests.post(url, auth=HTTPBasicAuth(name, password), json=data, headers=headers)
        user = User.objects.get(name=name)
        user.token = response.json().get('sha1')
        user.save()
        subject = "Account Creation"
        messageHTML = f"""
            <html>
            <head></head>
            <body>
                <p>Hi {user.name},</p>
                <p>Thank you for creating your account on GITMD!</p>
                <p>Your access token for git cloning your collections is: <strong>{user.token}</strong></p>
                <p>Use this token on your machine to access your files locally if you wish.</p>
            </body>
            </html>
            """
        messagePlain = (
            f"Hi {user.name},\n\n"
            f"Thank you for creating your account on GITMD!\n"
            f"Your access token for git cloning your collections is: {user.token}\n"
            "Use this token on your machine to access your files locally if you wish."
        )
        recipient = [email]
        email = EmailMultiAlternatives(subject, messagePlain, EMAIL_HOST_USER, recipient)
        email.attach_alternative(messageHTML, "text/html")
        email.send()

        if response.status_code != 201:
            return Response({'error': 'Failed to create user token'}, status=response.status_code)
        
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    
class Login(APIView):
    def post(self, request):
        email = request.data['email']
        password = request.data['password']
        user = User.objects.filter(email=email).first()
        if(user is None):
            raise AuthenticationFailed('User not found')
        
        if not(user.check_password(password)):
            raise AuthenticationFailed('Incorrect password')
        
        payload = {
            'id':user.id,
            'exp':datetime.datetime.utcnow() + datetime.timedelta(minutes=120),
            'iat':datetime.datetime.utcnow()
        }

        token = jwt.encode(payload, 'secret', algorithm='HS256')

        response = Response()
        response.set_cookie(key='jwt', value=token, httponly=True)
        response.data = {
            'jwt':token,
            'name' : user.name,
            'email' : user.email
        }
        return response
    
class UserView(APIView):

    def get(self, request):
        jwtToken = request.COOKIES.get('jwt')

        if not(jwtToken):
            raise AuthenticationFailed('User not authenticated')
        
        try:
            payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])

        except jwt.ExpiredSignatureError:
            raise AuthenticationFailed('User not authenticated')
        
        user = User.objects.filter(id=payload['id']).first()
        serializer = UserSerializer(user)
        return Response(serializer.data)
    
class Logout(APIView):

    def post(self, request):
        response = Response()
        response.delete_cookie('jwt')
        response.data = {
            'message' : 'Success'
        }
        return response
    
class PasswordReset(APIView):

    def put(self, request, format=None):

        email = request.data['email']
        user = User.objects.filter(email=email).first()
        type = request.data['type']
        response = Response()
        if(type == "reset"):
            resetToken = request.data['resetToken']
            if(resetToken != user.resetToken):

                response.data = {
                'message' : 'Incorrect Reset Token'
                }
                return response
            else:
                newPassword = request.data['newPassword']
                user.set_password(newPassword) 
                user.save()
                response = Response()
                response.data = {
                    'message' : 'Success'
                }
                return response
        else:
            characters = string.ascii_letters + string.digits
            resetToken = ''.join(random.choice(characters) for _ in range(16))
            user.resetToken = resetToken
            subject = "Password Reset"
            messageHTML = f"""
                <html>
                <head></head>
                <body>
                    <p>Hi {user.name},</p>
                    <p>Your token for resetting your password is: <strong>{user.resetToken}</strong></p>
                    <p>Use this token on our site to reset your password</p>
                </body>
                </html>
                """
            messagePlain = (
                f"Hi {user.name},\n\n"
                f"Your token for resetting your password is: {user.resetToken}\n\n"
                "Use this token on our site to reset your password"
            )
            recipient = [email]
            email = EmailMultiAlternatives(subject, messagePlain, EMAIL_HOST_USER, recipient)
            email.attach_alternative(messageHTML, "text/html")
            email.send()
            user.save()
            response.data = {
                'message' : 'Success'
            }
            return response
             
    
class GiteaAPIUtils:
    @staticmethod
    def make_owner_search_request(repoName, token):
        url = f"{BASE_URL}/repos/search?q=" + repoName
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)

        if response.status_code == 200:
            return response.json()["data"][0]["owner"]["login"]
        else:
            return Response({'error': 'Owner of repo not found'}, status=response.status_code)