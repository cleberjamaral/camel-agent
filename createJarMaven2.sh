!#/bin/bash

read -p "

ATTENTION: 
It is assuming you are in the branch 'maven2' of camel-agent repository.
On the root there is the 'master' branch with all project files.
You have built the jar using 'mvn install' on camel-agent-component sub-project


PRESS ANY KEY TO CONTINUE
"

mvn install:install-file -DgroupId=nz.ac.otago.infosci -DartifactId=camelagent -Dversion=1.1.3 -Dfile=../camel-agent/camel-agent-component/target/camelagent-1.1.3.jar -Dpackaging=jar -DlocalRepositoryPath=. -DcreateChecksum=true -DpomFile=../camel-agent/camel-agent-component/pom.xml

echo "

Process done!

"

