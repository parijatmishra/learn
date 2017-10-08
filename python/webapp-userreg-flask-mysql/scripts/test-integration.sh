#!/bin/bash
OLD_DIR="$(pwd)"
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "${SCRIPT_DIR}"
PROJECT_DIR="$( cd .. && pwd )"

#####################
export SQLALCHEMY_DATABASE_URI="mysql+pymysql://app:secret@localhost/simple_python_app"
export SECRET_KEY=456
export MAIL_SUPPRESS_SEND=0		# TODO: make this work
#####################

echo "Switching to [${PROJECT_DIR}] ..."
cd "${PROJECT_DIR}"

if [ ! -d "${PROJECT_DIR}"/venv ]; then
	echo "Deleting virtualenv at [${PROJECT_DIR}/venv] ..."	
	rm -rf "{${PROJECT_DIR}}/venv"
fi
echo "Creating virtualenv at [${PROJECT_DIR}/venv] ..."
virtualenv "${PROJECT_DIR}"/venv

. "${PROJECT_DIR}"/venv/bin/activate
echo "Installing dependencies from [${PROJECT_DIR}/dev_requirements.txt] ..."
pip --quiet --disable-pip-version-check install -r ${PROJECT_DIR}/dev_requirements.txt

python simple-python-app.py drop_tables && 		\
python simple-python-app.py db upgrade  && 		\
nosetests -w test/unit 							\
  --with-xunit --xunit-file=nosetests.xml		\
  --with-coverage --cover-package=app 			\
  --cover-html-dir="${PROJECT_DIR}"/out/test/unit/coverage.html
ret=$?

deactivate
cd "${OLD_DIR}"
exit $ret
