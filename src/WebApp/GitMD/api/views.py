from django.shortcuts import render
from rest_framework import generics, status
from .serializers import MarkdownFileSerializer
from .models import MarkdownFile
from rest_framework.views import APIView
from rest_framework.response import Response
from pathlib import Path
import os
from django.shortcuts import get_object_or_404

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