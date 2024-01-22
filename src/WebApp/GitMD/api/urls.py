from django.urls import path
from .views import MarkdownFileView, MarkdownFileCreate, MarkdownFileDelete, MarkdownFileDetails, MarkdownFileEdit

urlpatterns = [
    path('view', MarkdownFileView.as_view()),
    path('create', MarkdownFileCreate.as_view()),
    path('delete', MarkdownFileDelete.as_view()),
    path('edit', MarkdownFileEdit.as_view()),
    path('<str:code>', MarkdownFileDetails.as_view(), name='markdown_detail'),
]
