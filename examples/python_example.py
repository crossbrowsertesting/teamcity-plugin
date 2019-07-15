import unittest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import requests, time
import os

class SeleniumCBT(unittest.TestCase):
    def setUp(self):

        self.username = os.environ.get("CBT_USERNAME")
        self.authkey  = os.environ.get("CBT_APIKEY")

        self.api_session = requests.Session()
        self.api_session.auth = (self.username,self.authkey)
        self.test_result = None
        
        
        caps = {}

        caps['name'] = os.environ.get("CBT_BUILD_NAME")
        caps['os_api_name'] = os.environ.get("CBT_OPERATING_SYSTEM")
        caps['browser_api_name'] =os.environ.get("CBT_BROWSER")
        caps['screen_resolution'] = os.environ.get("CBT_RESOLUTION")
        caps['record_video'] = 'true'
        
              
        try:
            self.driver = webdriver.Remote(
            desired_capabilities=caps, 
            command_executor="http://%s:%s@hub.crossbrowsertesting.com:80/wd/hub"%(self.username, self.authkey))
  
        except Exception as e:
            raise e
    def test_CBT(self):
        try:   
            self.driver.get("http://crossbrowsertesting.github.io/selenium_example_page.html")

            time.sleep(10)

            self.assertEqual(self.driver.title, 'Selenium Test Example Page')
            
            self.test_result = 'pass'
        
        except AssertionError as e:
            self.drvier.quit()
            # log the error message, and set the score
            self.api_session.put('https://crossbrowsertesting.com/api/v3/selenium/' + self.driver.session_id + '/snapshots/' + snapshot_hash,
                data={'description':"AssertionError: " + str(e)})
            self.test_result = 'fail'

            raise
     
        self.driver.quit()
        # Here we make the api call to set the test's score
        # Pass if it passes, fail if an assertion fails, unset if the test didn't finish
        if self.test_result is not None:
            self.api_session.put('https://crossbrowsertesting.com/api/v3/selenium/' + self.driver.session_id,
                data={'action':'set_score', 'score':self.test_result})
          

           
if __name__ == '__main__':
    unittest.main()
