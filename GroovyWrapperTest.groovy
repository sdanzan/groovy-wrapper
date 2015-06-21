/**
 * Created by gon on 23/05/15.
 */

class GroovyWrapperTest extends GroovyTestCase {

    def targetScript = 'GroovyWrapper.groovy'
    def instance

    @Override
    void setUp() {
        GroovyClassLoader loader = new GroovyClassLoader()
        instance =  loader.parseClass(new File(targetScript)).newInstance()
    }

    void testExtractGrapesGradleNotation() {
        def lines = ["// some code here",
                     "@Grab('org.codehaus.groovy:groovy-all:2.4.3')",
                     "// other code here"]
        def results = instance.extractGrapes(lines)

        assert results == [['org.codehaus.groovy', 'groovy-all', '2.4.3']]
    }

    void testExtractGrapesIvyNotation() {
        def lines = ["// some code here",
                     "@Grab(group ='org.spockframework', module = 'spock-core', version = '1.0-groovy-2.4')",
                     "// other code here"]
        def results = instance.extractGrapes(lines)

        assert results == [['org.spockframework', 'spock-core', '1.0-groovy-2.4']]
    }

    void testExtractGrapesMixedNotation() {
        def lines = ["// some code here",
                     "@Grab(group ='org.spockframework', module = 'spock-core', version = '1.0-groovy-2.4')",
                     "@Grab('org.codehaus.groovy:groovy-all:2.4.3')",
                     "// other code here"]
        def results = instance.extractGrapes(lines)

        assert results == [['org.spockframework', 'spock-core', '1.0-groovy-2.4'], ['org.codehaus.groovy', 'groovy-all', '2.4.3']]
    }

    void testOnlyGrabDirective() {
        def lines = ["// some code here",
                     "@GrabResolve(name='codehaus', root='http://repository.codehaus.org/')",
                     "@Grab(group ='org.spockframework', module = 'spock-core', version = '1.0-groovy-2.4')",
                     "@Grab('org.codehaus.groovy:groovy-all:2.4.3')",
                     "// other code here"]
        def results = instance.extractGrapes(lines)

        assert results == [['org.spockframework', 'spock-core', '1.0-groovy-2.4'], ['org.codehaus.groovy', 'groovy-all', '2.4.3']]
    }


}
