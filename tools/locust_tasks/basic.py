"""
Each user makes a simple, single get on the server roor
"""
from locust import WebLocust, require_once
import random

def index(l):
    l.client.get("/")

class TestUser(WebLocust):
    host = "http://127.0.0.1:8080"
    tasks = [index]
    # wait between half a second and 3 seconds
    min_wait=500
    max_wait=3000