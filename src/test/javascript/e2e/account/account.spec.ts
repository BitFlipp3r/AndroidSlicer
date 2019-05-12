import { browser, element, by, ExpectedConditions as ec } from 'protractor';

import { NavBarPage, SignInPage } from '../page-objects/jhi-page-objects';

const expect = chai.expect;

describe('account', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage(true);
  });

  it('should fail to login with bad password', async () => {
    const expect1 = 'Welcome, Java Hipster!';
    const value1 = await element(by.css('h1')).getText();
    expect(value1).to.eq(expect1);
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'foo');

    const expect2 = 'Failed to sign in! Please check your credentials and try again.';
    const value2 = await element(by.css('.alert-danger')).getText();
    expect(value2).to.eq(expect2);
  });

  it('should login successfully with admin account', async () => {
    await browser.get('/');
    signInPage = await navBarPage.getSignInPage();

    const expect1 = 'Login';
    const value1 = await element(by.className('username-label')).getText();
    expect(value1).to.eq(expect1);
    await signInPage.autoSignInUsing('admin', 'admin');

    const expect2 = 'You are logged in as user "admin".';
    await browser.wait(ec.visibilityOf(element(by.id('home-logged-message'))));
    const value2 = await element(by.id('home-logged-message')).getText();
    expect(value2).to.eq(expect2);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
