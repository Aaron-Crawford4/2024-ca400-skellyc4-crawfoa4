from django.db import models
import string
import random
from django.contrib.auth.models import AbstractUser
import uuid

# Create your models here.

def generate_code():
    length = 8
    while(True):
        code = ''.join(random.choices(string.ascii_uppercase, k=length))
        if MarkdownFile.objects.filter(code=code).count() == 0:
            break
    return code

class MarkdownFile(models.Model):
    title = models.CharField(max_length=250)
    content = models.TextField()
    date_created = models.DateTimeField(auto_now_add=True)
    code = models.CharField(max_length=8, unique=True, default=generate_code)

    def __str__(self):
        return self

class User(AbstractUser):
    name = models.CharField(max_length=255)
    email = models.CharField(max_length=255, unique=True)
    password = models.CharField(max_length=255)
    username = None
    token = models.CharField(max_length=255, default="")

    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []