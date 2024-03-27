import requests
from django.test import TestCase
from rest_framework.test import APIClient
from django.contrib.auth import get_user_model

class RegisterTestCase(TestCase):
    def setUp(self):
        self.client = APIClient()

    def tearDown(self):
        user = get_user_model().objects.filter(email='John_Doe@example.com').first()
        if user:
            self.delete_user_on_gitea(user)

    def delete_user_on_gitea(self, user):
        url = f"http://gitea.gitmd.ie:80/api/v1/admin/users/{user.name}"
        headers = {
            'Authorization': f'token 9812c3c5008c1da5927f7ef20b45535116a8ee87',
        }
        response = requests.delete(url, headers=headers)
        if response.status_code != 204:
            print(f"Failed to delete user {user.email} on Gitea: {response.text}")

    def test_register_user(self):
        data = {
            'name': 'John_Doe',
            'email': 'John_Doe@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/register", data=data, format="json")

        self.assertEqual(response.status_code, 201)

        self.assertTrue(get_user_model().objects.filter(email='John_Doe@example.com').exists())

class UserTestCase(TestCase):
    def setUp(self):

        self.client = APIClient()
        data = {
            'name': 'John_Doe',
            'email': 'John_Doe@example.com',
            'password': 'test_password'
        }
        self.email = 'John_Doe@example.com'
        self.name ='John_Doe'

        response = self.client.post("/api/register", data=data, format="json")

        self.assertEqual(response.status_code, 201)

        self.assertTrue(get_user_model().objects.filter(email='John_Doe@example.com').exists())
        self.user = get_user_model().objects.filter(email='John_Doe@example.com')        

    def tearDown(self):
        user = get_user_model().objects.filter(email='John_Doe@example.com').first()
        if user:
            self.delete_user_on_gitea(user)

    def delete_user_on_gitea(self, user):
        url = f"http://gitea.gitmd.ie:80/api/v1/admin/users/{user.name}"
        headers = {
            'Authorization': f'token 9812c3c5008c1da5927f7ef20b45535116a8ee87',
        }
        response = requests.delete(url, headers=headers)
        if response.status_code != 204:
            print(f"Failed to delete user {user.email} on Gitea: {response.text}")

    def test_login_user(self):

        data = {
            'email': 'John_Doe@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        response = self.client.get("/api/user", headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.data['name'], self.name)
        self.assertEqual(response.data['email'], self.email)

    def test_get_user_unauthenticated(self):

        response = self.client.get("/api/user",  format="json")
        self.assertEqual(response.status_code, 403)

    def test_logout_user(self):

        response = self.client.post("/api/logout", format="json")
        self.assertEqual(response.status_code, 200)

class CreateTestCase(TestCase):
    def setUp(self):

        self.client = APIClient()
        data = {
            'name': 'John_Doe2',
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/register", data=data, format="json")

        self.assertEqual(response.status_code, 201)

        self.assertTrue(get_user_model().objects.filter(email='John_Doe2@example.com').exists())
        self.user = get_user_model().objects.filter(email='John_Doe2@example.com')

        data = {
            'name': 'John_Doe3',
            'email': 'John_Doe3@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/register", data=data, format="json")

        self.assertEqual(response.status_code, 201)

        self.assertTrue(get_user_model().objects.filter(email='John_Doe3@example.com').exists())          

    def tearDown(self):
        user = get_user_model().objects.filter(email='John_Doe2@example.com').first()
        if user:
            self.file_delete()
            self.file_restore()
            self.file_delete()
            self.deleted_files_get()
            self.repo_delete()
            self.delete_user_on_gitea(user)

    def delete_user_on_gitea(self, user):
        url = f"http://gitea.gitmd.ie:80/api/v1/admin/users/John_Doe2"
        headers = {
            'Authorization': f'token 9812c3c5008c1da5927f7ef20b45535116a8ee87',
        }
        response = requests.delete(url, headers=headers)
        if response.status_code != 204:
            print(f"Failed to delete user John_Doe2 on Gitea: {response.text}")

        url = f"http://gitea.gitmd.ie:80/api/v1/admin/users/John_Doe3"
        headers = {
            'Authorization': f'token 9812c3c5008c1da5927f7ef20b45535116a8ee87',
        }
        response = requests.delete(url, headers=headers)
        if response.status_code != 204:
            print(f"Failed to delete user John_Doe3 on Gitea: {response.text}")

    def test_repo_create(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoTitle': 'testRepo',
            'title': 'testTitle',
            'content': 'test_content'
        }

        response = self.client.post("/api/create", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 201)
        self.repo_get()

    def test_file_create(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoTitle': 'testRepo',
            'title': 'testTitle',
            'content': 'test_content'
        }

        response = self.client.post("/api/create", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 201)
        self.file_get()
        self.file_details_and_edit()
        self.add_collaborator()
        self.list_collaborators()
        self.remove_collaborator()
        self.file_previous_versions()

    def file_details_and_edit(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        response = self.client.get("/api/John_Doe2/testRepo/testTitle.md", headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)
        sha = response.json()['sha']
        data = {
            'repo': 'testRepo',
            'file': 'testTitle.md',
            'user': 'John_Doe2',
            'sha': sha,
            'content': 'test_content2'
        }

        response = self.client.put("/api/edit", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def file_previous_versions(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repo': 'testRepo',
            'file': 'testTitle.md',
            'owner': 'John_Doe2',
        }

        response = self.client.post("/api/previousVersions", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def file_restore(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'file': 'testTitle.md',
        }

        response = self.client.post("/api/restoreFile", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def add_collaborator(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repo': 'testRepo',
            'addedUser': 'John_Doe3',
            'repoFullName': 'John_Doe2/testRepo'
        }

        response = self.client.put("/api/addUserToRepo", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 204)
    
    def list_collaborators(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'repoFullName': 'John_Doe2/testRepo'
        }

        response = self.client.post("/api/collaborators", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)
    
    def remove_collaborator(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'collaborator': 'John_Doe3',
            'repoFullName': 'John_Doe2/testRepo'
        }

        response = self.client.post("/api/removeCollaborator", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def file_get(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'switch': "files",
        }

        response = self.client.post("/api/view", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def repo_get(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'switch': "repo",
        }

        response = self.client.post("/api/view", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def deleted_files_get(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repoName': 'testRepo',
            'switch': "deletedFiles",
        }

        response = self.client.post("/api/view", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)

    def repo_delete(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repo': 'testRepo',
        }

        response = self.client.post("/api/repoDelete", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 204)

    def file_delete(self):

        data = {
            'email': 'John_Doe2@example.com',
            'password': 'test_password'
        }

        response = self.client.post("/api/login", data=data, format="json")
        self.assertEqual(response.status_code, 200)
        self.jwt_token = response.cookies['jwt'].value

        data = {
            'repo': 'testRepo',
            'file': 'testTitle.md',
            'sha': ''
        }

        response = self.client.post("/api/delete", data=data, headers={'Cookie': f'jwt={self.jwt_token}'}, format="json")
        self.assertEqual(response.status_code, 200)