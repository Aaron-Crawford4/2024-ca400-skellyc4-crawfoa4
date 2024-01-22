# Generated by Django 4.2.5 on 2024-01-03 15:33

import api.models
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('api', '0003_alter_markdownfile_code'),
    ]

    operations = [
        migrations.AlterField(
            model_name='markdownfile',
            name='code',
            field=models.CharField(default=api.models.generate_code, max_length=8, unique=True),
        ),
    ]