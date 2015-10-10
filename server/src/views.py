from __init__ import app, db
from flask import request


@app.route("/")
def index():
    return "Ayy lmao"


@app.route("/register")
def register():
    obj = request.get_json(force=True)

    username, phone, password = obj["username"], obj["phone_number"], obj["password"]

    errors = validate(obj, "username", "phone_number", "password")

    if errors:
        return "".join([error.__repr__() for error in errors])

    return username + " " + phone + " " + password


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
    errors = validate(req_json, "post", "category", "event", "author", "auth")
    if errors:
        print errors
        return "validation error", 401

    # Todo act on authentication
    if not authenticate(req_json["auth"]):
        return "authentication error", 401

    post_collection = db['posts']

    post = {
        "author": req_json["author"],
        "post": req_json["post"],
        "category": req_json["category"]
        # event object?
    }

    post_id = post_collection.insert_one(post)

    for each in post_collection.find():
        print each

    return "{}".format(post_id.inserted_id), 200


def authenticate(req):
    return True
