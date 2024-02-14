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

# Create your views here.

class MarkdownFileView(APIView):
    serializer_class = MarkdownFileSerializer

    def get(self, request, format=None):
        markdownFiles = MarkdownFile.objects.all()
        serializer = self.serializer_class(markdownFiles, many=True)
        return Response(serializer.data)


class MarkdownFileCreate(APIView):
    serializer_class = MarkdownFileSerializer

    def post(self, request, format=None):
        serializer = self.serializer_class(data=request.data)
        i = 0
        if serializer.is_valid():
            title = serializer.data.get('title')
            content = serializer.data.get('content')
            queryset = MarkdownFile.objects.filter(title=title)
            if queryset.exists():
                markdownFile = queryset[0]
                markdownFile.content = content
                markdownFile.save(update_fields=['content', 'title'])
                i = 1
            else:
                markdownFile = MarkdownFile(title=title, content=content)
                markdownFile.save()
                i = 2
        filename = title + ".md"
        p = Path('media')
        p.mkdir(parents=True, exist_ok=True)
        with (p / filename).open('w') as markdown_file:
            markdown_file.write(content)
        if i == 0:
            return Response({'Bad Request': 'Invalid data...'}, status=status.HTTP_400_BAD_REQUEST)
        elif i == 1:
            return Response(MarkdownFileSerializer(markdownFile).data, status=status.HTTP_200_OK)
        elif i == 2:
            return Response(MarkdownFileSerializer(markdownFile).data, status=status.HTTP_201_CREATED)
        
class MarkdownFileEdit(APIView):
    serializer_class = MarkdownFileSerializer

    def put(self, request, format=None):
        code = request.data.get('code')
        markdown_file = get_object_or_404(MarkdownFile, code=code)
        old_title = markdown_file.title

        serializer = self.serializer_class(markdown_file, data=request.data)
        if serializer.is_valid():
            serializer.save()
            old_file_path = os.path.join('media', f"{old_title}.md")
            new_title = request.data.get('title')
            new_file_path = os.path.join('media', f"{new_title}.md")
            os.rename(old_file_path, new_file_path)
            with open(new_file_path, 'w') as file:
                file.write(request.data.get('content'))

            return Response(serializer.data, status=status.HTTP_200_OK)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class MarkdownFileDelete(APIView):
    serializer_class = MarkdownFileSerializer

    def post(self, request, format=None):
        title = request.data.get('title')
        queryset = MarkdownFile.objects.filter(title=title)
        if queryset.exists():
            markdownFile = queryset[0]
            serialized_data = MarkdownFileSerializer(markdownFile).data
            markdownFile.delete()
            filename = title + ".md"
            p = Path('media')
            p.mkdir(parents=True, exist_ok=True)
            os.remove(p / filename)
            return Response(serialized_data, status=status.HTTP_200_OK)
        return Response({'Bad Request': 'Invalid data or resource not found.'}, status=status.HTTP_400_BAD_REQUEST)

class MarkdownFileDetails(APIView):
    serializer_class = MarkdownFileSerializer

    def get(self, request, code, format=None):
        markdown_file = get_object_or_404(MarkdownFile, code=code)
        serializer = self.serializer_class(markdown_file)
        return Response(serializer.data)

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
            'name': 'auth_key'
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
        token = request.COOKIES.get('jwt')

        if not(token):
            raise AuthenticationFailed('User not authenticated')
        
        try:
            payload = jwt.decode(token, 'secret', algorithms=['HS256'])

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