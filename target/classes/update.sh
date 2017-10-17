#!/bin/bash
#



package_Name='/opt/flow/download/flow-web-admin/'
if [ ! -x "$package_Name" ]; then
     mkdir -p "$package_Name"  
fi
package_Version='flow-web-admin'
source_File='/opt/update/src/'
config_Dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/webapps/flow-web-admin/WEB-INF/classes'
destination_Dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/webapps/'
bak_dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/'
temporary_Filename=${package_Name}${package_Version}.war
project_Filename='flow-web-admin'
bak_project_Filename='flow-web-admin'
project_warName='flow-web-admin.war'
bak_project_warName='flow-web-admin-bak.war'
basepath=$(cd `dirname $0`; pwd)
Id=""
httpUrl=""
  if [ -f "$config_Dir/param.properties" ];then
         . $config_Dir/param.properties
           Id="$id"
           httpUrl="$httpurl"
           echo "id=$Id"  
  fi
update()
	{
	cd ${config_Dir} && ./web.sh stop
	if test $? -eq 0; then
		echo "${project_Filename} is stopped ok.."
	fi
	
	cd ${destination_Dir} 
        rm -f ${project_warName}
         cd ${bak_dir}
          if [ -d ${bak_project_Filename} ]; then
             rm -rf ${bak_project_Filename}
          fi
	 cd ${destination_Dir}  
	 mv  ${project_Filename}  ${bak_dir}
	 cd ${destination_Dir} 
	 rm -rf ${project_Filename} 
	 cd ${destination_Dir} 
	 rm -f ${project_warName}
	if [ -d ../${bak_project_Filename} ]; then
		echo "Backup ${project_Filename} is succeed."
	fi
	
	cp -f ${temporary_Filename} ${destination_Dir}
	cd ${destination_Dir}
	unzip -o ${temporary_Filename} -d ${project_Filename}
	cd ${destination_Dir}
	if [ -d ${project_Filename} ]; then
		cp -f ../${bak_project_Filename}/WEB-INF/classes/*.properties ${project_Filename}/WEB-INF/classes/
		cd ${config_Dir} 
		chmod -R 744 $config_Dir/*.sh
		echo "update $project_Filename is succeed."
	else
	    
		echo "update ${project_Filename} is failed."
	fi
	
	cd ${config_Dir} && ./web.sh start
	 echo 'tomcat重启状态为:'$?
     echo "id=$Id"  
     echo "httpUrl=$httpUrl"  
	 if test $? -eq 0;then
     echo 'tomcat重启成功啦！！！！！'
	 echo -e $project_Filename $project_Filename
	 echo -e $project_Filename $project_Filename
	 echo $destination_Dir
	 echo $config_Dir
	 cd ${destination_Dir}
	 [ -n "${bak_project_warName}" ] && rm -f ${bak_project_warName}
	 cd ${destination_Dir}
     [ -n "${project_warName}" ] && rm -f ${project_warName}
	 cd ${package_Name}
	
	 curl -l -H "Content-type: application/json" -X POST -d '{"id":'$Id',"status":"2"}' ${httpUrl//'\:'/':'}
	 else 
      curl -l -H "Content-type: application/json" -X POST -d '{"id":'$Id',"status":"3"}' ${httpUrl//'\:'/':'}
	  rollback
	 fi
}
rollback(){
cd ${config_Dir} && ./web.sh stop

if test $? -eq 0; then
		echo "${project_Filename} is stopped ok.."
	fi
cd ${destination_Dir} && mv -f ${destination_Dir}${bak_project_warName} ${destination_Dir}${project_warName}
if [ -d ${project_warName} ]; then
     rm -rf ${destination_Dir}${project_Filename}
		echo "rollback ${project_Filename} is succeed."
	fi
cd ${config_Dir} && ./web.sh start
}

main()
{
	update
}

main
exit 0
