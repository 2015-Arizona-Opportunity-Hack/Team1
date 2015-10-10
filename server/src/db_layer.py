from pymongo import MongoClient
from werkzeug.security import generate_password_hash, check_password_hash

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
        Model.to_doc(self)


class User(Model):

    def __init__(self, object_dict=None, **kwargs):
        Model.__init__(self)
        if object_dict is None:
            self.username = kwargs["username"]
            self.phone_number = kwargs["phone_number"]
            self.password_hash = generate_password_hash(kwargs["password_hash"], "pbkdf2:sha256:10000")
        else:
            self.username = object_dict["username"]
            self.phone_number = object_dict["phone_number"]
            self.password_hash = object_dict["password_hash"]

    @classmethod
    def COLLECTION_NAME(cls):
        return "users"

    def to_doc(self):
        return {"username": self.username, "phone_number": self.phone_number, "password_hash": self.password_hash}

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
        collection = self.db.get_collection(model_cls)
        return collection.insert_one(model_inst.to_doc()).inserted_id

    def find(self, inst_id, model_cls):
        collection = self.get_collection(model_cls)
        return model_cls.__class__(collection.find_one({"_id": inst_id}))
