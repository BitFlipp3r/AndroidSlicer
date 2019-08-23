/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { CFAOptionComponentsPage, CFAOptionUpdatePage } from './cfa-option.page-object';

const expect = chai.expect;

describe('CFAOption e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let cFAOptionUpdatePage: CFAOptionUpdatePage;
  let cFAOptionComponentsPage: CFAOptionComponentsPage;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load CFAOptions', async () => {
    await navBarPage.goToEntity('cfa-option');
    cFAOptionComponentsPage = new CFAOptionComponentsPage();
    await browser.wait(ec.visibilityOf(cFAOptionComponentsPage.title), 5000);
    expect(await cFAOptionComponentsPage.getTitle()).to.eq('CFA Options');
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
