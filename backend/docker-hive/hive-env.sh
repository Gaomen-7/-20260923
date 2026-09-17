# WARNING: This file is bind-mounted into hive-server container.
# After editing this file, run: docker compose restart hive-server
# Then verify: docker exec hive-server ps aux | grep file.encoding

# Set HADOOP_HOME to point to a specific hadoop install directory
export HADOOP_HOME=/opt/hadoop-2.7.4

# Hive Configuration Directory can be controlled by:
export HIVE_CONF_DIR=/opt/hive/conf

# Folder containing extra libraries required for hive compilation/execution can be controlled by:
# export HIVE_AUX_JARS_PATH=

# Set JVM encoding to UTF-8 for Chinese character support
# NOTE: Use HADOOP_OPTS only (JVM args). HIVE_OPTS passes to command line, not JVM.
export HADOOP_OPTS="$HADOOP_OPTS -Dfile.encoding=UTF-8"

# Set locale
export LANG=C.UTF-8
export LC_ALL=C.UTF-8
