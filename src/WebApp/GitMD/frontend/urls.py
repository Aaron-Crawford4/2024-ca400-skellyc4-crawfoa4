from django.urls import path
from .views import index

urlpatterns = [
    path('', index),
    path('<str:view>', index),
    path('login', index),
    path('create', index),
    path('create/<str:repo>', index),
    path('<str:user>/<str:repo>/<str:file>', index),
    path('edit/<str:user>/<str:repo>/<str:file>', index),
    path('help', index),
]
