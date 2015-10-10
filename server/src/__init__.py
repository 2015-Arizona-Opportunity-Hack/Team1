from flask import Flask
from db_layer import GideonDatabaseClient

app = Flask(__name__)
db = GideonDatabaseClient()

from views import *
