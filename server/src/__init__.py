from flask import Flask

app = Flask(__name__)

from flask.ext.sqlalchemy import SQLAlchemy
from pymongo import MongoClient

# db = SQLAlchemy(app)
client = MongoClient()
db = client['core-db']

from views import *
