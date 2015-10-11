from __init__ import app, db
from flask import request, json
from db_layer import User, Post, SuperUser
from util import validate, authenticate
from flask import render_template

@app.route("/")
def index():
    hover = {"index":"","news-alerts":"","login":"","users":"","emergency":""}
    return render_template('index.html',hover)  # :(


@app.route("/example")
def example():
    return json.dumps([x.to_doc() for x in db.get_last_n_of_class(Post, 5)]), 200


@app.route("/login/")
def admin_login():
    hover = {"index":"","news-alerts":"","login":"","users":"","emergency":""}
    return render_template('login.html')


@app.route("/news-alerts/")
def news_alerts():
    hover = {"index":"","news-alerts":"","login":"","users":"","emergency":""}
    hover["news-alerts"] = "active"
    return render_template('news-alerts.html',hover=hover)


@app.route("/urgent-alerts/")
def urgent_alerts():
    hover = {"index":"","news-alerts":"","login":"","users":"","emergency":""}
    hover["urgent-alerts"] = "active"
    return render_template('emergency-alerts.html',hover=hover)


@app.route("/users/")
def users():
    hover = {"index":"","news-alerts":"","login":"","users":"","emergency":""}
    hover["users"] = "active"
    return render_template('users.html',hover=hover)


@app.route("/register_su", methods=["POST"])
def register_su():
    obj = request.get_json(force=True)

    print obj
    errors = validate(obj, "email", "first_name", "last_name", "password")
    if errors:
        print errors
        return "validation error", 401

    email, first_name, last_name, password = obj["email"], obj["first_name"], obj["last_name"], obj["password"]

    new_su = SuperUser(email=email, first_name=first_name, last_name=last_name, password=password)
    db.insert(new_su)

    return json.dumps({"auth_token": new_su.generate_auth_token()})


@app.route("/login_su", methods=["POST"])
def login_su():
    obj = request.get_json(force=True)

    print obj
    errors = validate(obj, "email", "password")
    if errors:
        print errors
        return "validation_error", 401

    email, password = obj["email"], obj["password"]

    su = db.find_by_field("email", email, SuperUser)

    if not su:
        print "su not found", 404

    if not su.verify_password(password):
        print "invalid username or password"

    return json.dumps({"auth_token": su.generate_auth_token()})


@app.route("/register", methods=["POST"])
def register():
    obj = request.get_json(force=True)

    print obj

    errors = validate(obj, "email", "phone_number", "password", "language_pref", "first_name", "last_name")
    if errors:
        print errors
        return "validation error", 401

    email, phone, password, language_pref, first_name, last_name = obj["email"], obj["phone_number"], obj["password"], \
                                                                   obj["language_pref"], obj["first_name"], obj[
                                                                       "last_name"]

    new_user = User(email=email, phone_number=phone, password=password, language_pref=language_pref,
                    first_name=first_name, last_name=last_name)
    db.insert(new_user)

    return json.dumps({"auth_token": new_user.generate_auth_token()}), 200


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

    errors = validate(req_json, "posts", "categories", "event", "author", "auth")
    if errors:
        print errors
        return "validation error", 401

    new_post = Post(author=req_json["author"], posts=req_json["posts"], categories=req_json["categories"],
                    event=req_json["event"])
    db.insert(new_post)

    return "Success", 200


@app.route("/usr_prop", methods=['POST'])
def usr_prop():
    req_json = request.get_json(force=True)

    errors = validate(req_json, "property", "value", "action_token")
    if errors:
        print errors
        return "validation error", 401

    if req_json["property"] != "email" and req_json["property"] != "language_pref":
        usr = db.find_by_field("email", req_json["email"], User)
        if not usr:
            return "user not found", 404

        elif not usr.verify_action_token(req_json["action_token"]):
            return "action_token invalid", 401

        else:
            setattr(usr, req_json["property"], req_json["value"])
            db.update(usr)

            print req_json["property"] + " " + req_json["value"]

            return "Success", 200
    else:
        return "bad property", 401
