from django.urls import path
from .views import MarkdownFileView, MarkdownFileCreate, MarkdownFileDelete, MarkdownFileDetails, MarkdownFileEdit

urlpatterns = [
    path('view', MarkdownFileView.as_view(), name='markdown_view'),
    path('create', MarkdownFileCreate.as_view(), name='markdown_create'),
    path('delete', MarkdownFileDelete.as_view(), name='markdown_delete'),
    path('edit', MarkdownFileEdit.as_view(), name='markdown_edit'),
    path('<str:code>', MarkdownFileDetails.as_view(), name='markdown_detail'),
]
