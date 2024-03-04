from django.urls import path
from .views import MarkdownFileView, MarkdownFileCreate, MarkdownFileDelete, MarkdownFileDetails, MarkdownFileEdit, Register, Login, Logout, UserView, RepoDelete, AddUserToRepo

urlpatterns = [
    path('register', Register.as_view(), name='user_register'),
    path('login', Login.as_view(), name='user_login'),
    path('logout', Logout.as_view(), name='user_logout'),
    path('user', UserView.as_view()),
    path('view', MarkdownFileView.as_view(), name='markdown_view'),
    path('create', MarkdownFileCreate.as_view(), name='markdown_create'),
    path('delete', MarkdownFileDelete.as_view(), name='markdown_delete'),
    path('repoDelete', RepoDelete.as_view(), name='repo_delete'),
    path('addUserToRepo', AddUserToRepo.as_view(), name='Adding_user_to_repo'),
    path('edit', MarkdownFileEdit.as_view(), name='markdown_edit'),
    path('<str:username>/<str:repo>/<str:file>', MarkdownFileDetails.as_view(), name='markdown_detail'),
]
