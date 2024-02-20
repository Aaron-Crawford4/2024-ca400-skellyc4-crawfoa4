from django.urls import path
from .views import index

urlpatterns = [
    path('', index),
    path('Create', index),
    path('Create/<str:repo>', index),
    path('<str:user>/<str:repo>/<str:file>', index),
    path('Edit/<str:user>/<str:repo>/<str:file>', index),
]
