#
# Copyright 2002-2007 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# prefix assumed in script files
prefix=/usr/local

# files that need mode 755
EXEC_FILES=groovyWrapper.sh

#files that need mode 644
SCRIPT_FILES =GroovyWrapper.groovy


all:
	@echo "usage: make install"
	@echo "       make uninstall"

check: GROOVY-EXISTS

GROOVY-EXISTS: ; @which groovy > /dev/null

install: check
	install -d -m 0755 $(prefix)/bin
	install -d -m 755 $(prefix)/share/groovy/lib
	install -m 0755 $(EXEC_FILES) $(prefix)/bin
	install -m 0644 $(SCRIPT_FILES) $(prefix)/share/groovy/lib

uninstall:
	test -d $(prefix)/bin && \
	cd $(prefix)/bin && \
	rm -f $(EXEC_FILES)

	test -d $(prefix)/share/groovy/lib && \
    cd $(prefix)/share/groovy/lib && \
    rm -f $(SCRIPT_FILES)