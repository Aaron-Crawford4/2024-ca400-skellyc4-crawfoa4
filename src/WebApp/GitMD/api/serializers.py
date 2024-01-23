from .models import MarkdownFile
from rest_framework import serializers

class MarkdownFileSerializer(serializers.ModelSerializer):
    class Meta:
        model = MarkdownFile
        fields = '__all__'
