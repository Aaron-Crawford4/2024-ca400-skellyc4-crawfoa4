from django.urls import path
from .views import index

urlpatterns = [
    path('', index),
    path('Create', index),
    path('<str:code>', index),
    path('Edit/<str:code>', index),
]
