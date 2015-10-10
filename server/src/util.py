from functools import wraps
from flask import request

def authenticate(func):
    @wraps(func)
    def auth_call(*args, **kwargs):
        if request.json:  # TODO STEVE IMPLEMENT AUTH (Auth currently based on request.json existing)
            return func(*args, **kwargs)
        else:
            return "Authentication Failed", 401
    return auth_call

def validate(obj, *args):
    args = set(args)
    errors = ()
    for required in args:
        if required not in obj:
            errors = errors + ((required + " is required"),)
    return errors