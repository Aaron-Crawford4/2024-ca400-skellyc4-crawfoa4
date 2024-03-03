from django.test import TestCase
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient
from .models import MarkdownFile
import json

class MarkdownFileViewTests(TestCase):

    def setUp(self):
        MarkdownFile.objects.create(title='Test File 1', content='Content 1', code='ASDFGHJK')
        # MarkdownFile.objects.create(title='Test File 2', content='Content 2', code='ZXCVBNML')
        url = reverse('markdown_create')
        data = {'title': 'Test File 2', 'content': 'Content 2', 'code' : 'ZXCVBNML'}
        count = MarkdownFile.objects.count()
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(MarkdownFile.objects.count(), count + 1)

    def test_list_markdown_files(self):
        url = reverse('markdown_view')
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(len(response.data), 2)

    # not working yet
    # def test_edit_markdown_file(self):
    #     url = reverse('markdown_edit')
    #     headers = {'Content-Type': 'application/json'}
    #     data = {'title': 'Updated Title', 'content': 'Updated Content', 'code' : 'ZXCVBNML'}
    #     response = self.client.put(url, json.dumps(data), content_type='application/json', **headers)
    #     self.assertEqual(response.status_code, status.HTTP_200_OK)
    #     updated_file = MarkdownFile.objects.get(code='ZXCVBNML')
    #     self.assertEqual(updated_file.title, 'Updated Title')
    #     self.assertEqual(updated_file.content, 'Updated Content')

    def test_delete_markdown_file(self):
        url = reverse('markdown_delete')
        data = {'title': 'Test File 2'}
        response = self.client.post(url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(MarkdownFile.objects.count(), 1)

    def test_get_markdown_file_details(self):
        url = reverse('markdown_detail', args=['ASDFGHJK'])
        response = self.client.get(url)
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertEqual(response.data['title'], 'Test File 1')
