from django.shortcuts import render

# Create your views here.
def index(request, *Args, **kwargs):
    return render(request, 'frontend/index.html')