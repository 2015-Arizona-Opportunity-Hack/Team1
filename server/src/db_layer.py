from pymongo import MongoClient


# Magic decorator for defining constants
def constant(f):
    def fset(self, value):
        raise TypeError

    def fget(self):
        return f()

    return property(fget, fset)


class Model:
    def __init__(self):
        pass

    @staticmethod
    @constant
    def COLLECTION_NAME():
        return Model.__name__


class Post(Model):
    def __init__(self):
        Model.__init__(self)

    @staticmethod
    @constant
    def COLLECTION_NAME():
        return "posts"


class GideonDatabaseClient:
    @staticmethod
    @constant
    def DATABASE_NAME():
        return "test-database"

    def __init__(self):
        self.client = MongoClient("mongodb://localhost:27017/")
        self.db = self.client[GideonDatabaseClient.DATABASE_NAME()]

    def get_collection(self, model_cls):
        return self.db[model_cls.COLLECTION_NAME()]
