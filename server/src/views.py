from __init__ import app, db
from flask import request, json
from db_layer import User, Post
from util import validate, authenticate


@app.route("/")
def index():
    return "Ayy lmao"


@app.route("/register", methods=["POST"])
def register():
    obj = request.get_json(force=True)

    print obj

    errors = validate(obj, "email", "phone_number", "password", "language_pref", "first_name", "last_name")
    if errors:
        print errors
        return "validation error", 401

    email, phone, password, language_pref, first_name, last_name = obj["email"], obj["phone_number"], obj["password"], obj["language_pref"], obj["first_name"], obj["last_name"]

    new_user = User(email=email, phone_number=phone, password=password, language_pref=language_pref, first_name=first_name, last_name=last_name)
    db.insert(new_user)

    return email + " " + phone, 200


@app.route("/login", methods=["POST"])
def login():
    obj = request.get_json(force=True)

    print obj

    errors = validate(obj, "email", "password")
    if errors:
        print errors
        return "validation error", 401

    email, password = obj["email"], obj["password"]

    user = db.find_by_field("email", email, User)

    if not user:
        return "email not found", 401

    if not user.verify_password(password):
        return "Incorrect password", 401

    else:
        return json.dumps({"auth_token": user.generate_auth_token()})


def validate(obj, *args):
    args = set(args)
    errors = ()
    for required in args:
        if required not in obj:
            errors = errors + ((required + " is required"),)
    return errors


@app.route("/make_post", methods=['POST'])
@authenticate
def make_post():
    req_json = request.get_json(force=True)

    # Todo act on validation
    errors = validate(req_json, "posts", "categories", "event", "author", "auth")
    if errors:
        print errors
        return "validation error", 401

    new_post = Post(author=req_json["author"], posts=req_json["posts"], categories=req_json["categories"], event=req_json["event"])
    db.insert(new_post)

    return "Success", 200
