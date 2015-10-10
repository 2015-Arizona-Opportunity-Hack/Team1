from __init__ import app, db
from flask import request
from db_layer import User, Post

@app.route("/")
def index():
    return "Ayy lmao"


@app.route("/register", methods=["POST"])
def register():
    obj = request.json

    print obj

    username, phone, password = obj["username"], obj["phone_number"], obj["password"]

    errors = validate(obj, "username", "phone_number", "password")
    if errors:
        print errors
        return "validation error", 401

    new_user = User(username=username, phone_number=phone, password=password)
    db.insert(new_user)

    return username + " " + phone, 200


def validate(obj, *args):
    args = set(args)
    errors = ()
    for required in args:
        if required not in obj:
            errors = errors + ((required + " is required"),)
    return errors


@app.route("/make_post", methods=['POST'])
def make_post():
    req_json = request.json

    # Todo act on validation
    errors = validate(req_json, "posts", "categories", "event", "author", "auth")
    if errors:
        print errors
        return "validation error", 401

    # Todo act on authentication
    if not authenticate(req_json["auth"]):
        return "authentication error", 401

    new_post = Post(author=req_json["author"], posts=req_json["posts"], categories=req_json["categories"], event=req_json["event"])
    db.insert(new_post)

    return "Success", 200


def authenticate(req):
    return True
