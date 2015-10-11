from pymongo import MongoClient
from werkzeug.security import generate_password_hash, check_password_hash
from grumpy import verify_token, generate_secret, generate_token
from datetime import datetime, timedelta


class Model:
    def __init__(self):
        pass

    @classmethod
    def COLLECTION_NAME(cls):
        return Model.__name__

    def to_doc(self):
        return {}


class Post(Model):
    def __init__(self, object_dict=None, id=None, **kwargs):
        Model.__init__(self)
        if object_dict is None:
            self.author = kwargs["author"]
            self.posts = kwargs["posts"]
            self.categories = kwargs["categories"]
            self.event = kwargs["event"]
        else:
            self.author = object_dict["author"]
            self.posts = object_dict["posts"]
            self.categories = object_dict["categories"]
            self.event = object_dict["event"]
            self.id = object_dict["_id"]

    @classmethod
    def COLLECTION_NAME(cls):
        return "posts"

    def to_doc(self):
        return {
            "author": self.author,
            "posts": self.posts,
            "categories": self.categories,
            "event": self.event
        }


class User(Model):
    def __init__(self, object_dict=None, id=None, **kwargs):
        Model.__init__(self)
        if object_dict is None:
            self.username = kwargs["username"]
            self.first_name = kwargs["first_name"]
            self.last_name = kwargs["last_name"]
            self.phone_number = kwargs["phone_number"]
            self.password_hash = generate_password_hash(kwargs["password"], "pbkdf2:sha256:10000")
            self.language_pref = kwargs["language_pref"]
            self.auth_token_secret = generate_secret(128)
            self.action_token_secret = generate_secret(128)
        else:
            self.username = object_dict["username"]
            self.first_name = object_dict["first_name"]
            self.last_name = object_dict["last_name"]
            self.phone_number = object_dict["phone_number"]
            self.password_hash = object_dict["password_hash"]
            self.language_pref = object_dict["language_pref"]
            self.auth_token_secret = object_dict["auth_token_secret"]
            self.action_token_secret = object_dict["action_token_secret"]
            self.id = object_dict["_id"]

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

    def generate_auth_token(self):
        return generate_token(self.username, datetime.utcnow(), 0, str(self.auth_token_secret))

    def generate_action_token(self):
        return generate_token(self.username, datetime.utcnow(), 0, str(self.action_token_secret))

    def verify_auth_token(self, token):
        return generate_token(token, 0, timedelta(days=120), str(self.auth_token_secret))

    def verify_action_token(self, token):
        return generate_token(token, 0, timedelta(minutes=10), str(self.action_token_secret))


    @classmethod
    def COLLECTION_NAME(cls):
        return "users"

    def to_doc(self):
        return {
            "username": self.username,
            "first_name": self.first_name,
            "last_name": self.last_name,
            "phone_number": self.phone_number,
            "password_hash": self.password_hash,
            "language_pref": self.language_pref,
            "auth_token_secret": self.auth_token_secret,
            "action_token_secret": self.action_token_secret
        }

class Update(Model):
    def __init__(self, object_dict=None, id=None, **kwargs):
        Model.__init__(self)
        if object_dict is None:
            self.author = kwargs["phone_number"]
            self.posts = kwargs["username"]
        else:
            self.author = object_dict["phone_number"]
            self.posts = object_dict["username"]
            self.id = object_dict["_id"]

    @classmethod
    def COLLECTION_NAME(cls):
        return "updates"

    def to_doc(self):
        return {
            "phone_number": self.author,
            "username": self.posts
        }


class GideonDatabaseClient:
    @classmethod
    def DATABASE_NAME(cls):
        return "test-database"

    def __init__(self):
        self.client = MongoClient("mongodb://localhost:27017/")
        self.db = self.client[GideonDatabaseClient.DATABASE_NAME()]

    def get_collection(self, model_cls):
        return self.db[model_cls.COLLECTION_NAME()]

    def insert(self, model_inst):
        model_cls = model_inst.__class__
        collection = self.get_collection(model_cls)
        return collection.insert_one(model_inst.to_doc()).inserted_id

    def find(self, inst_id, model_cls):
        collection = self.get_collection(model_cls)
        model_data = collection.find_one({"_id": inst_id})
        if model_data:
            return model_cls(model_data)
        else:
            return None

    def findByField(self, inst_query_fieldname, inst_query_value, model_cls):
        collection = self.get_collection(model_cls)
        model_data = collection.find_one({inst_query_fieldname: inst_query_value})
        if model_data:
            return model_cls(model_data)
        else:
            return None

    def update(self, model_inst):
        collection = self.get_collection(model_inst.__class__)
        collection.update(
            {"_id": model_inst.id},
            model_inst.to_doc()
        )
