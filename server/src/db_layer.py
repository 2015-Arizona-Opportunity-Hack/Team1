from pymongo import MongoClient
from werkzeug.security import generate_password_hash, check_password_hash
from grumpy import verify_token, generate_secret, generate_token


class Model:
    def __init__(self):
        pass

    @classmethod
    def COLLECTION_NAME(cls):
        return Model.__name__

    def to_doc(self):
        return {}


class Post(Model):
    def __init__(self, obj_dict=None, **kwargs):
        Model.__init__(self)
        if obj_dict is None:
            self.author = kwargs["author"]
            self.posts = kwargs["posts"]
            self.categories = kwargs["categories"]
            self.event = kwargs["event"]
        else:
            self.author = obj_dict["author"]
            self.posts = kwargs["posts"]
            self.categories = kwargs["categories"]
            self.event = kwargs["event"]

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
    def __init__(self, object_dict=None, **kwargs):
        Model.__init__(self)
        if object_dict is None:
            self.username = kwargs["username"]
            self.phone_number = kwargs["phone_number"]
            self.password_hash = generate_password_hash(kwargs["password"], "pbkdf2:sha256:10000")
            self.language_pref = kwargs["language_pref"]
            self.auth_token_secret = generate_secret(128)
            self.action_token_secret = generate_secret(128)

        else:
            self.username = object_dict["username"]
            self.phone_number = object_dict["phone_number"]
            self.password_hash = object_dict["password_hash"]
            self.language_pref = object_dict["language_pref"]
            self.auth_token_secret = object_dict["auth_token_secret"]
            self.action_token_secret = object_dict["action_token_secret"]

    @classmethod
    def COLLECTION_NAME(cls):
        return "users"

    def to_doc(self):
        return {
                    "username": self.username,
                    "phone_number": self.phone_number,
                    "password_hash": self.password_hash,
                    "language_pref": self.language_pref,
                    "auth_token_secret": self.auth_token_secret,
                    "action_token_secret": self.action_token_secret
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
        return model_cls.__class__(collection.find_one({"_id": inst_id}))

    def findByField(self, inst_query_fieldname, inst_query_value, model_cls):
        collection = self.get_collection(model_cls)
        return model_cls.__class__(collection.find_one({inst_query_fieldname: inst_query_value}))
