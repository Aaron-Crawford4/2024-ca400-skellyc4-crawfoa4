from django.urls import path
from .views import MarkdownFileView, MarkdownFileCreate, MarkdownFileDelete, MarkdownFileDetails, MarkdownFileEdit, Register, Login, Logout, UserView

urlpatterns = [
    path('register', Register.as_view(), name='user_register'),
    path('login', Login.as_view(), name='user_login'),
    path('logout', Logout.as_view(), name='user_logout'),
    path('user', UserView.as_view()),
    path('view', MarkdownFileView.as_view(), name='markdown_view'),
    path('create', MarkdownFileCreate.as_view(), name='markdown_create'),
    path('delete', MarkdownFileDelete.as_view(), name='markdown_delete'),
    path('edit', MarkdownFileEdit.as_view(), name='markdown_edit'),
    path('<str:username>/<str:repo>/<str:file>', MarkdownFileDetails.as_view(), name='markdown_detail'),
]
