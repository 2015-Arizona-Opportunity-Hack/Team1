from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
from pymongo import MongoClient

app = Flask(__name__)
# db = SQLAlchemy(app)
client = MongoClient()
db = client['core-db']

from views import *
