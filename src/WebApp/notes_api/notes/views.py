from django.http import HttpResponse
from django.shortcuts import render
from .models import Note
from rest_framework import viewsets
from .serializers import NoteSerializer
import os
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse

# Create your views here.

class NotesViewset(viewsets.ModelViewSet):

    serializer_class = NoteSerializer
    queryset = Note.objects.all()

    def perform_create(self, serializer):
        instance = serializer.save()
        filename = f"{instance.title}.md"
        # Save the content to a Markdown file
        with open("../media/" + filename, "w") as markdown_file:
            markdown_file.write(instance.content)
        instance.markdown_file = filename
        instance.save()
    
    def perform_destroy(self, instance):
            
        filename = f"{instance.title}.md"
        # delete the Markdown file
        os.remove("../media/" + filename)

        # Delete the note
        instance.delete()

    @csrf_exempt
    def display_markdown(self, filename):
        markdown_dir = '../media/'

        file_path = os.path.join(markdown_dir, filename)

        if os.path.exists(file_path):
            with open(file_path, 'r') as file:
                markdown_content = file.read()
            return HttpResponse(markdown_content)
        else:
            return HttpResponse('File not found', status=404)