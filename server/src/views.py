from __init__ import app, db
from flask import request
from db_layer import User, Post
from util import validate, authenticate

@app.route("/", methods=["POST"])
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

@app.route("/make_post", methods=['POST'])
@authenticate
def make_post():
    req_json = request.json

    # Todo act on validation
    errors = validate(req_json, "posts", "categories", "event", "author", "auth")
    if errors:
        print errors
        return "validation error", 401

    new_post = Post(author=req_json["author"], posts=req_json["posts"], categories=req_json["categories"], event=req_json["event"])
    a = db.insert(new_post)

    b = db.find(a, Post)
    print b
    db.update(b)

    return "Success", 200

