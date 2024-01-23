from django.urls import path
from .views import MarkdownFileView, CreateMarkdownFileView

urlpatterns = [
    path('markdownfile', MarkdownFileView.as_view()),
    path('create', CreateMarkdownFileView.as_view()),
]
