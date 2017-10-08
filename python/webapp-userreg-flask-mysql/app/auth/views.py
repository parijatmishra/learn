from flask import render_template, flash, redirect, request, url_for
from flask.ext.login import login_user, logout_user, current_user, login_required
from . import auth_blueprint
from forms import LoginForm, RegistrationForm
from app.models import User
from app import db
from app.email import send_email

@auth_blueprint.route('/register', methods=['GET', 'POST'])
def register():
    form = RegistrationForm()
    if form.validate_on_submit():
        user = User(
            username=form.username.data,
            email=form.email.data,
            password=form.password.data
            )
        db.session.add(user)
        db.session.commit()
        token = user.generate_confirmation_token()
        send_email(user.email, 'Confirm Your Account',
            'auth/email/confirm', user=user, token=token)
        flash("A confirmation email has been sent to your email address."
              "Please click on the link in it to confirm your account.")
        return redirect(url_for('main.index'))
    return render_template('auth/register.html', form=form)

@auth_blueprint.route('/login', methods=['GET', 'POST'])
def login():
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(email=form.email.data).first()
        if user is not None and user.verify_password(form.password.data):
                login_user(user, form.remember_me.data)
                flash('You were logged in.')
                return redirect(request.args.get('next') or url_for('main.index'))
        else:
            flash('Invalid email or password')
    return render_template('auth/login.html', form=form)

@auth_blueprint.route('/logout', methods=['GET'])
def logout():
    logout_user()
    flash('You have been logged out.')
    return redirect(url_for('main.index'))

@auth_blueprint.before_app_request
def before_request():
    """Logged in but unconfirmed users are sent to a confirmation warning page.

    Non-logged in users are not redirected: they will get access denied or
    pages will show content for anonymous users.
    """
    if current_user.is_authenticated \
      and not current_user.confirmed \
      and request.endpoint[:5] != 'auth.' \
      and request.endpoint != 'static':
        return redirect(url_for('auth.unconfirmed'))

@auth_blueprint.route('/unconfirmed')
def unconfirmed():
    if current_user.is_anonymous or current_user.confirmed:
        return redirect(url_for('main.index'))
    return render_template('auth/unconfirmed.html')

@auth_blueprint.route('/confirm')
def resend_confirmation():
    token = current_user.generate_confirmation_token()
    send_email(current_user.email, 'Confirm Your Account',
        'auth/email/confirm', user=current_user, token=token)
    flash('A new confirmation email has been sent to you.')
    return redirect(url_for('main.index'))

@auth_blueprint.route('/confirm/<token>')
@login_required
def confirm(token):
    if current_user.confirmed:
        return redirect(url_for('main.index'))
    if current_user.confirm(token):
        flash('You have confirmed your account. Thanks!')
    else:
        flash('The confirmation link is invalid or has expired.')
    return redirect(url_for('main.index'))


