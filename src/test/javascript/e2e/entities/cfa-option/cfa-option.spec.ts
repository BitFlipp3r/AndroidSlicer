/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { CFAOptionComponentsPage, CFAOptionDeleteDialog, CFAOptionUpdatePage } from './cfa-option.page-object';

const expect = chai.expect;

describe('CFAOption e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let cFAOptionUpdatePage: CFAOptionUpdatePage;
  let cFAOptionComponentsPage: CFAOptionComponentsPage;
  let cFAOptionDeleteDialog: CFAOptionDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load CFAOptions', async () => {
    await navBarPage.goToEntity('cfa-options');
    cFAOptionComponentsPage = new CFAOptionComponentsPage();
    await browser.wait(ec.visibilityOf(cFAOptionComponentsPage.title), 5000);
    expect(await cFAOptionComponentsPage.getTitle()).to.eq('CFA Options');
  });

  it('should load create CFAOption page', async () => {
    await cFAOptionComponentsPage.clickOnCreateButton();
    cFAOptionUpdatePage = new CFAOptionUpdatePage();
    expect(await cFAOptionUpdatePage.getPageTitle()).to.eq('Create or edit a CFA Option');
    await cFAOptionUpdatePage.cancel();
  });

  it('should create and save CFAOptions', async () => {
    const nbButtonsBeforeCreate = await cFAOptionComponentsPage.countDeleteButtons();

    await cFAOptionComponentsPage.clickOnCreateButton();
    await promise.all([
      cFAOptionUpdatePage.typeSelectLastOption(),
      cFAOptionUpdatePage.setKeyInput('key'),
      cFAOptionUpdatePage.setDescriptionInput('description'),
      cFAOptionUpdatePage.setCfaLevelInput('5')
    ]);
    expect(await cFAOptionUpdatePage.getKeyInput()).to.eq('key', 'Expected Key value to be equals to key');
    expect(await cFAOptionUpdatePage.getDescriptionInput()).to.eq('description', 'Expected Description value to be equals to description');
    expect(await cFAOptionUpdatePage.getCfaLevelInput()).to.eq('5', 'Expected cfaLevel value to be equals to 5');
    const selectedIsDefault = cFAOptionUpdatePage.getIsDefaultInput();
    if (await selectedIsDefault.isSelected()) {
      await cFAOptionUpdatePage.getIsDefaultInput().click();
      expect(await cFAOptionUpdatePage.getIsDefaultInput().isSelected(), 'Expected isDefault not to be selected').to.be.false;
    } else {
      await cFAOptionUpdatePage.getIsDefaultInput().click();
      expect(await cFAOptionUpdatePage.getIsDefaultInput().isSelected(), 'Expected isDefault to be selected').to.be.true;
    }
    await cFAOptionUpdatePage.save();
    expect(await cFAOptionUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await cFAOptionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last CFAOption', async () => {
    const nbButtonsBeforeDelete = await cFAOptionComponentsPage.countDeleteButtons();
    await cFAOptionComponentsPage.clickOnLastDeleteButton();

    cFAOptionDeleteDialog = new CFAOptionDeleteDialog();
    expect(await cFAOptionDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this CFA Option?');
    await cFAOptionDeleteDialog.clickOnConfirmButton();

    expect(await cFAOptionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
