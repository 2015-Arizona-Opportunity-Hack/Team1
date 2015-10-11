from __init__ import app, db
from flask import request, json, redirect, url_for, make_response
from db_layer import User, Post, SuperUser
from util import validate, authenticate
from flask import render_template
from threading import Thread
from SMS import send_sms_to_all_users
from Email import send_message_to_all_users
from werkzeug.security import generate_password_hash


@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "GET":
        return render_template("login.html")
    elif request.method == "POST":
        email = request.form["username"]
        password = request.form["password"]

        user = db.find_by_field("email", email, SuperUser)

        if not user: # user lookup failed
            return redirect("/")

        if not user.verify_password(password):
            return redirect("/")

        else:
            resp = make_response(redirect("/news-alerts/"))
            resp.set_cookie("session", user.generate_auth_token())
            return resp


@app.route("/example")
def example():
    return json.dumps({"news": [x.to_doc() for x in db.get_last_n_of_class(Post, 5)]}), 200


@app.route("/news-alerts/", methods=["GET", "POST"])
def news_alerts():
    if request.method == "GET":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        user = db.find_by_field("email", email, SuperUser)

        if not user:
            return redirect("/")

        if not user.verify_auth_token(session):
            return redirect("/")

        new_session = user.generate_auth_token()

        resp = make_response(render_template("news-alerts.html"))
        resp.set_cookie("session", new_session)

        return resp

    elif request.method == "POST":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        user = db.find_by_field("email", email, SuperUser)

        if not user:
            return redirect("/")

        if not user.verify_auth_token(session):
            return redirect("/")

        print request["title_english"]

        new_session = user.generate_auth_token()

        resp = make_response(render_template("news-alerts.html"))
        resp.set_cookie("session", new_session)

        return resp



@app.route("/urgent-alerts/", methods=["GET", "POST"])
def urgent_alerts():
    if request.method == "GET":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        user = db.find_by_field("email", email, SuperUser)

        if not user:
            return redirect("/")

        if not user.verify_auth_token(session):
            return redirect("/")

        new_session = user.generate_auth_token()

        resp = make_response(render_template("emergency-alerts.html"))
        resp.set_cookie("session", new_session)

        return resp

    elif request.method == "POST":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        user = db.find_by_field("email", email, SuperUser)

        if not user:
            return redirect("/")

        if not user.verify_auth_token(session):
            return redirect("/urgent-analysis/")

        new_session = user.generate_auth_token()

        text_english = request.form["message_english"]
        text_spanish = request.form["message_spanish"]

        post = Post(author=user.to_doc(), categories=[], event=None, posts=[{"lang": "en", "body": text_english}, {"lang": "es", "body": text_spanish}])

        db.insert(post)

        send_urgent_alert(post)
        return redirect("/")


@app.route("/users/new", methods=["GET", "POST"])
def users_new():
    if request.method == "GET":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        su = db.find_by_field("email", email, SuperUser)

        if not su:
            return redirect("/")

        if not su.verify_auth_token(session):
            return redirect("/")

        new_session = su.generate_auth_token()
        resp = make_response(render_template("users.html", user=None))
        resp.set_cookie("session", new_session)

        return resp

    elif request.method == "POST":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        email = session.split(":")[1]

        su = db.find_by_field("email", email, SuperUser)

        if not su:
            return redirect("/")

        if not su.verify_auth_token(session):
            return redirect("/")

        errors = validate(request.form, "email", "password", "first_name", "last_name", "phone_number")
        if errors:
            new_session = su.generate_auth_token()
            resp = make_response(render_template("users.html", user=None))
            resp.set_cookie("session", new_session)
            return resp

        else:
            email = request.form["email"]
            if db.find_by_field("email", email, User):
                new_session = su.generate_auth_token()
                resp = make_response(render_template("users.html", user=None))
                resp.set_cookie("session", new_session)
                return resp
            else:
                user = User(email=email, password=request.form["password"], first_name=request.form["first_name"],
                            last_name=request.form["last_name"], phone_number=request.form["phone_number"],
                            language_pref="en", message_prefs="2")
                db.insert(user)
                new_session = su.generate_auth_token()
                resp = make_response(redirect("/users/" + email))
                resp.set_cookie("session", new_session)
                return resp


@app.route("/users/<string:email>", methods=["GET", "POST", "DELETE"])
def users(email):
    if request.method == "GET":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        su_email = session.split(":")[1]

        su = db.find_by_field("email", su_email, SuperUser)

        if not su:
            return redirect("/")

        if not su.verify_auth_token(session):
            return redirect("/")

        new_session = su.generate_auth_token()

        user = db.find_by_field("email", email, User)

        resp = make_response(render_template("users.html", user=user))
        resp.set_cookie("session", new_session)

        return resp

    elif request.method == "POST":
        session = request.cookies.get("session")
        if not session:
            return redirect("/")

        su_email = session.split(":")[1]

        su = db.find_by_field("email", su_email, SuperUser)

        if not su:
            return redirect("/")

        if not su.verify_auth_token(session):
            return redirect("/")

        user = db.find_by_field("email", email, User)

        return render_template("users.html", user=user)

@app.route("/users/emaillookup", methods=["POST"])
def lookup():
    if not admin_auth(request.cookies.get("session")):
        return redirect("/") # FAILED ATUH
    user = db.find_by_field("email", request.form['email'], User)
    return render_template("users.html", user=user)

@app.route("/users/updateuser", methods=["POST"])
def updateuser():
    user = db.find_by_field("email", request.form['email'], User)

    if not admin_auth(request.cookies.get("session")):
        return redirect("/") # FAILED ATUH

    if not User:
        return render_template("users.html", user=None) # TODO NO USER FOUND
    user.phone_number = request.form['phone_number']
    user.first_name = request.form['first_name']
    user.last_name = request.form['last_name']
    db.update(user)
    return render_template("users.html", user=user)

@app.route("/users/updatepass", methods=["POST"])
def updatepass():
    user = db.find_by_field("email", request.form['email'], User)

    if not admin_auth(request.cookies.get("session")):
        return redirect("/") # FAILED ATUH

    if not User:
        return render_template("users.html", user=None)  # TODO NO USER FOUND
    user.password = generate_password_hash(request.form['password'], "pbkdf2:sha256:10000")
    db.update(user)
    return render_template("users.html", user=user)

@app.route("/users/delusr", methods=["POST"])
def delusr():
    user = db.find_by_field("email", request.form['email'], User)

    if not admin_auth(request.cookies.get("session")):
        return redirect("/") # FAILED ATUH

    if not User:
        return render_template("users.html", user=None)  # TODO NO USER FOUND
    db.remove(user)
    return render_template("users.html", user=user)

@app.route("/login/")
def login():
    if request.cookies.get("session"):
        resp = make_response(redirect("/"))
        resp.set_cookie("session", "")
        return resp
    else:
        return redirect("/")


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

# THIS ROUTE IS NO LONGER BEING USED PER STEVE
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
        return "su not found", 404

    if not su.verify_password(password):
        return "invalid password", 401

    return json.dumps({"auth_token": su.generate_auth_token()})


@app.route("/register", methods=["POST"])
def register():
    obj = request.get_json(force=True)

    print obj

    errors = validate(obj, "email", "phone_number", "password", "language_pref", "first_name", "last_name")
    if errors:
        print errors
        return "validation error", 401

    email, phone, password, language_pref, first_name, last_name, message_prefs = obj["email"], obj["phone_number"], \
                                                                                  obj["password"], \
                                                                                  obj["language_pref"], obj[
                                                                                      "first_name"], obj[
                                                                                      "last_name"], "2"

    new_user = User(email=email, phone_number=phone, password=password, language_pref=language_pref,
                    first_name=first_name, last_name=last_name, message_prefs=message_prefs)
    db.insert(new_user)

    return json.dumps({"auth_token": new_user.generate_auth_token()}), 200


@app.route("/login_app", methods=["POST"])
def login_app():
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

    if req_json["property"] == "message_prefs" or req_json["property"] == "language_pref":
        email = req_json["action_token"].split(":")[1]
        usr = db.find_by_field("email", email, User)
        if not usr:
            return "user not found", 404

        elif not usr.verify_action_token(req_json["action_token"]):
            return "action_token invalid", 401

        else:
            setattr(usr, req_json["property"], req_json["value"])
            db.update(usr)

            return json.dumps({"action_token": usr.generate_action_token()})
    else:
        return "bad property", 401


@app.route("/user_lookup", methods=['POST'])
def user_lookup():
    req_json = request.get_json(force=True)

    errors = validate(req_json, "user", "admin_token", "admin_email")
    if errors:
        print errors
        return "validation error", 401

    admin = db.find_by_field("email", req_json["admin"], SuperUser)
    if not admin:
        return "Superuser account invalid", 401
    else:
        if not admin.verify_auth_token(req_json["admin_token"]):
            return "Token invalid", 401

    usrdata = db.find_by_field("email", req_json["user"], User)

    return json.dumps(usrdata.to_doc())


@app.route("/del_user", methods=['POST'])
def del_user():
    req_json = request.get_json(force=True)

    errors = validate(req_json, "user", "admin_token", "admin_email")
    if errors:
        print errors
        return "validation error", 401

    admin = db.find_by_field("email", req_json["admin"], SuperUser)
    if not admin:
        return "Superuser account invalid", 401
    else:
        if not admin.verify_auth_token(req_json["admin_token"]):
            return "Token invalid", 401

    usrdata = db.find_by_field("email", req_json["user"], User)
    db.remove(usrdata)
    return "Success", 200


@app.route("/request_action_token", methods=["POST"])
def request_action_token():
    req_json = request.get_json(force=True)

    errors = validate(req_json, "auth_token")
    if errors:
        print errors
        return "validation error", 401

    email = req_json["auth_token"].split(":")[1]

    user = db.find_by_field("email", email, User)

    if not user:
        return "User not found", 404

    return json.dumps({"action_token": user.generate_action_token(), "auth_token": user.generate_auth_token()})


def send_urgent_alert(post):
    thread1 = Thread(target=send_sms_to_all_users(post))
    thread2 = Thread(target=send_message_to_all_users(post))
    thread1.start()
    thread2.start()


def admin_auth(session):
    email = session.split(":")[1]

    su = db.find_by_field("email", email, SuperUser)

    if not su:
        return False

    if not su.verify_auth_token(session):
        return False
    return True