package org.jasig.services.persondir.core.config;

import static org.mockito.Mockito.when;

import org.jasig.services.persondir.spi.CriteriaSearchableAttributeSource;
import org.jasig.services.persondir.spi.SimpleAttributeSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest {
    @Mock private SimpleAttributeSource requestAttributesDao;
    @Mock private CriteriaSearchableAttributeSource uPortalAccountUserSource;
    @Mock private CriteriaSearchableAttributeSource uPortalJdbcUserSource;
    @Mock private CriteriaSearchableAttributeSource ldapPersonAttributeDao;
    //postGradSurveyTermAttributeSource
    @Mock private SimpleAttributeSource seniorGraduationStatusAttributeSource;
    @Mock private SimpleAttributeSource mastersGraduationStatusAttributeSource;
    @Mock private SimpleAttributeSource graduationSurveyStatusAttributeSource;
    @Mock private SimpleAttributeSource heritageCheckAttributeSource;
    
//    @Mock private AttributeSourceFilter postGraduateSurveyFilter;
//    @Mock private AttributeSourceFilter isGraduatingFilter;
//    @Mock private AttributeSourceFilter hasUddsFilter;
    
    @Mock private BeanFactory beanFactory;
    
    @Test
    public void testConfigBuilder() {
        final PersonDirectoryBuilder configBuilder = PersonDirectoryConfigFactory.newPersonDirectoryBuilder("username");
        
        configBuilder
            .setMergeCacheName("org.jasig.services.persondir.USER_INFO.merge")
            ;
        
        when(requestAttributesDao.getAvailableAttributes()).thenReturn(ImmutableSet.of(
                "remoteUser",
                "remoteName",
                "cn",
                "eduWisconsinHRPersonID",
                "eduWisconsinHRSEmplID",
                "eduWisconsinOIMUserID",
                "eduWisconsinSPVI",
                "eduWisconsinTelephoneNumberExtension",
                "eduWisconsinUDDS",
                "givenName",
                "initials",
                "sn",
                "eppn"));
        configBuilder
            .addAttributeSource(requestAttributesDao)
                .addRequiredAttribute("remoteUser")
                .addAttributeMapping("remoteUser", "username")
                .addAttributeMapping("cn", "cn")
                .addAttributeMapping("cn", "displayName")
                .addAttributeMapping("eduWisconsinHRSEmplID", "hrPersonID")
                .addAttributeMapping("eduWisconsinHRSEmplID", "eduWisconsinHRSEmplID")
                .addAttributeMapping("eppn", "eppn")
                .addAttributeMapping("eppn", "username")
                .addAttributeMapping("eduWisconsinSPVI", "spvi")
                .addAttributeMapping("eduWisconsinTelephoneNumberExtension", "telephoneNumber")
                .addAttributeMapping("initials", "middleName")
                ;
        
        
        configBuilder
            .addAttributeSource(uPortalAccountUserSource)
                .setMaxResults(100)
                .addOptionalAttribute(
                        "username",
                        "givenName",
                        "sn")
            ;
        
        
        configBuilder
            .addAttributeSource(uPortalJdbcUserSource)
                .setResultCacheName("org.jasig.services.persondir.USER_INFO.up_user")
                .setMaxResults(100)
                .addRequiredAttribute("username")
                .addAttributeMapping("USER_NAME", "username")
            ;
        
        
        configBuilder
            .addAttributeSource(ldapPersonAttributeDao)
            .setResultCacheName("org.jasig.services.persondir.USER_INFO.ldap")
            .setMaxResults(100)
            .addOptionalAttribute(
                    "displayName",
                    "givenName",
                    "mail",
                    "sn",
                    "uid",
                    "wisceduadvisorflag",
                    "wisceduappttype",
                    "wisceduinstructorflag",
                    "wisceduisisemplid",
                    "wisceduisisinstructoremplid",
                    "wisceduisisnonstudentemplid",
                    "wisceduisisstudentemplid",
                    "wisceduhrpersonid",
                    "wiscEduHRSEmplid",
                    "wisceduphotoid",
                    "wiscedupvi",
                    "wiscedustudentgroup",
                    "wiscedustudentid",
                    "wiscedustudentstatus",
                    "wwid")
            .addAttributeMapping("uid", "username")
            .addAttributeMapping("wiscEduHRSEmplid", "wisceduhrpersonid")
            .addAttributeMapping("wiscEduHRSEmplid", "wiscEduHRSEmplid")
            ;
        
        
        configBuilder
            .addAttributeSource(seniorGraduationStatusAttributeSource)
//            .addFilter(postGraduateSurveyFilter)
            .setResultCacheName("org.jasig.services.persondir.USER_INFO.graduating_senior")
            .addRequiredAttribute("ID")
            .addRequiredAttribute("Term")
            .addAttributeMapping("ID", "wisceduisisstudentemplid")
            .addAttributeMapping("Term", "postGraduateSurveyTerm")
            ;
        
        
        configBuilder
            .addAttributeSource(mastersGraduationStatusAttributeSource)
//            .addFilter(postGraduateSurveyFilter)
            .setResultCacheName("org.jasig.services.persondir.USER_INFO.graduating_masters")
            .addRequiredAttribute("m.id")
            .addRequiredAttribute("m.term")
            .addAttributeMapping("m.id", "wisceduisisstudentemplid")
            .addAttributeMapping("m.term", "postGraduateSurveyTerm")
            ;
        
        
        configBuilder
            .addAttributeSource(graduationSurveyStatusAttributeSource)
//            .addFilter(postGraduateSurveyFilter, isGraduatingFilter)
            .setResultCacheName("org.jasig.services.persondir.USER_INFO.graduating_senior_survey")
            .addRequiredAttribute("pvi")
            .addAttributeMapping("pvi", "wiscedupvi")
            ;
        
        
        configBuilder
            .addAttributeSource(heritageCheckAttributeSource)
//            .addFilter(hasUddsFilter)
            .setResultCacheName("org.jasig.services.persondir.USER_INFO.heritage_check")
            .addRequiredAttribute("person_id")
            .addAttributeMapping("person_id", "wisceduhrpersonid")
            ;
        
        
        configBuilder.build(beanFactory);
    }
}
