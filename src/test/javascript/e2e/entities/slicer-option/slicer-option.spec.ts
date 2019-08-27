/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { SlicerOptionComponentsPage } from './slicer-option.page-object';

const expect = chai.expect;

describe('SlicerOption e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let slicerOptionComponentsPage: SlicerOptionComponentsPage;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load SlicerOptions', async () => {
    await navBarPage.goToEntity('slicer-options');
    slicerOptionComponentsPage = new SlicerOptionComponentsPage();
    await browser.wait(ec.visibilityOf(slicerOptionComponentsPage.title), 5000);
    expect(await slicerOptionComponentsPage.getTitle()).to.eq('Slicer Options');
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
