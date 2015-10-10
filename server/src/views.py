from __init__ import app
from flask import request, json


@app.route("/")
def index():
    return "SWAG"


@app.route("/register")
def register():
    obj = request.get_json(force=True)

    validate(obj, "username", "phone_number", "")


def validate(obj, *args):
    args = set(args)
    errors = ()
    for required in args:
        if required not in obj:
            errors = errors + ((required + "is required"),)
