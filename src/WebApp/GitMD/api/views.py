from django.shortcuts import render
from rest_framework import generics, status
from .serializers import MarkdownFileSerializer, UserSerializer
from .models import MarkdownFile, User
from rest_framework.views import APIView
from rest_framework.response import Response
from pathlib import Path
import os
from django.shortcuts import get_object_or_404
from rest_framework.exceptions import AuthenticationFailed
import jwt, datetime
import requests
from requests.auth import HTTPBasicAuth
import base64

# Create your views here.

class MarkdownFileView(APIView):
    serializer_class = MarkdownFileSerializer

    def post(self, request, format=None):
        markdownFiles = MarkdownFile.objects.all()

        jwtToken = request.COOKIES.get('jwt')
        switch = request.data["switch"]
        repoName = request.data["repoName"]
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        name = user.name

        if(switch == "repo"):
            url = "http://localhost:3000/api/v1/user/repos"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {
            }
            response = requests.get(url, json=data, headers=headers)
            #print(response.text)

            serializer = self.serializer_class(markdownFiles, many=True)

        else:
            url = "http://localhost:3000/api/v1/repos/" + name + "/" + repoName + "/contents"
            headers = {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
            data = {
            }
            response = requests.get(url, json=data, headers=headers)

        return Response(response.json())


class MarkdownFileCreate(APIView):
    serializer_class = MarkdownFileSerializer

    def post(self, request, format=None):
        serializer = self.serializer_class(data=request.data)

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        username = user.name

        if serializer.is_valid():
            repoTitle = request.data.get('repoTitle')
            title = serializer.data.get('title')
            content = serializer.data.get('content')
            encoded_bytes = base64.b64encode(content.encode('utf-8'))
            content = encoded_bytes.decode('utf-8')
            markdownFile = MarkdownFile(title=title, content=content)
            markdownFile.save()

        url = "http://localhost:3000/api/v1/user/repos"
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'name' : repoTitle,
        }
        response = requests.post(url, json=data, headers=headers)
        if response.status_code != 201: 
            return Response({'error': 'Failed to create user repo'}, status=response.status_code)

        filename = title + ".md"
        url = "http://localhost:3000/api/v1/repos/" + username + "/" + repoTitle + "/contents/" + filename
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'token ' + token
        }
        data = {
            'content' : content
        }
        response = requests.post(url, json=data, headers=headers)
        print(response.text)
        if response.status_code == 201:
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response({'error': 'Failed to create user file'}, status=response.status_code)

        
class MarkdownFileEdit(APIView):
    serializer_class = MarkdownFileSerializer

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
        url = "http://localhost:3000/api/v1/repos/" + user + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'content' : content,
            'sha' : sha,
        }
        response = requests.put(url, json=data, headers=headers)
        print(response.status_code)
        if response.status_code != 200: 
            return Response({'error': 'Failed to update user file'}, status=response.status_code)

        return Response(response.json(), status=status.HTTP_200_OK)


class MarkdownFileDelete(APIView):
    serializer_class = MarkdownFileSerializer

    def post(self, request, format=None):
        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        user = request.data.get('user')
        file = request.data.get('file')
        repo = request.data.get('repo')
        sha = request.data.get('sha')
        url = "http://localhost:3000/api/v1/repos/" + user + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        data = {
            'sha' : sha,
        }
        response = requests.delete(url, json=data, headers=headers)
        print(response.status_code)
        if response.status_code != 200: 
            return Response({'error': 'Failed to delete user file'}, status=response.status_code)

        return Response(response.json(), status=status.HTTP_200_OK)

class MarkdownFileDetails(APIView):
    serializer_class = MarkdownFileSerializer

    def get(self, request, username, repo, file, format=None):

        jwtToken = request.COOKIES.get('jwt')
        payload = jwt.decode(jwtToken, 'secret', algorithms=['HS256'])
        user = User.objects.filter(id=payload['id']).first()
        token = user.token
        url = "http://localhost:3000/api/v1/repos/" + username + "/" + repo + "/contents/" + file
        headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
        response = requests.get(url, headers=headers)
        if response.status_code == 200:
            return Response(response.json(), status=status.HTTP_200_OK)
        else:
            return Response({'error': 'Failed to get file content'}, status=response.status_code)

class Register(APIView):

    def post(self, request):
        serializer = UserSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save()

        name = request.data['name']
        email = request.data['email']
        password = request.data['password']

        url = 'http://localhost:3000/api/v1/admin/users'

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
        
        url = 'http://localhost:3000/api/v1/users/' + name + '/tokens'

        headers = {
            'Content-Type': 'application/json',
        }

        data = {
            'name': 'auth_key',
            'scopes': ['write:issue', 'write:notification', 'write:repository', 'write:user']
        }
        response = requests.post(url, auth=HTTPBasicAuth(name, password), json=data, headers=headers)
        print("Token = " + response.json().get('sha1'))
        user = User.objects.get(name=name)
        user.token = response.json().get('sha1')
        user.save()

        if response.status_code == 201:
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response({'error': 'Failed to create user token'}, status=response.status_code)
    
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
            'exp':datetime.datetime.utcnow() + datetime.timedelta(minutes=60),
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
        print(response)
        print(response.data)
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