/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { SlicerOptionComponentsPage, SlicerOptionDeleteDialog, SlicerOptionUpdatePage } from './slicer-option.page-object';

const expect = chai.expect;

describe('SlicerOption e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let slicerOptionUpdatePage: SlicerOptionUpdatePage;
  let slicerOptionComponentsPage: SlicerOptionComponentsPage;
  let slicerOptionDeleteDialog: SlicerOptionDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load SlicerOptions', async () => {
    await navBarPage.goToEntity('slicer-option');
    slicerOptionComponentsPage = new SlicerOptionComponentsPage();
    await browser.wait(ec.visibilityOf(slicerOptionComponentsPage.title), 5000);
    expect(await slicerOptionComponentsPage.getTitle()).to.eq('Slicer Options');
  });

  it('should load create SlicerOption page', async () => {
    await slicerOptionComponentsPage.clickOnCreateButton();
    slicerOptionUpdatePage = new SlicerOptionUpdatePage();
    expect(await slicerOptionUpdatePage.getPageTitle()).to.eq('Create or edit a Slicer Option');
    await slicerOptionUpdatePage.cancel();
  });

  it('should create and save SlicerOptions', async () => {
    const nbButtonsBeforeCreate = await slicerOptionComponentsPage.countDeleteButtons();

    await slicerOptionComponentsPage.clickOnCreateButton();
    await promise.all([
      slicerOptionUpdatePage.typeSelectLastOption(),
      slicerOptionUpdatePage.setKeyInput('key'),
      slicerOptionUpdatePage.setDescriptionInput('description')
    ]);
    expect(await slicerOptionUpdatePage.getKeyInput()).to.eq('key', 'Expected Key value to be equals to key');
    expect(await slicerOptionUpdatePage.getDescriptionInput()).to.eq(
      'description',
      'Expected Description value to be equals to description'
    );
    const selectedIsDefault = slicerOptionUpdatePage.getIsDefaultInput();
    if (await selectedIsDefault.isSelected()) {
      await slicerOptionUpdatePage.getIsDefaultInput().click();
      expect(await slicerOptionUpdatePage.getIsDefaultInput().isSelected(), 'Expected isDefault not to be selected').to.be.false;
    } else {
      await slicerOptionUpdatePage.getIsDefaultInput().click();
      expect(await slicerOptionUpdatePage.getIsDefaultInput().isSelected(), 'Expected isDefault to be selected').to.be.true;
    }
    await slicerOptionUpdatePage.save();
    expect(await slicerOptionUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await slicerOptionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last SlicerOption', async () => {
    const nbButtonsBeforeDelete = await slicerOptionComponentsPage.countDeleteButtons();
    await slicerOptionComponentsPage.clickOnLastDeleteButton();

    slicerOptionDeleteDialog = new SlicerOptionDeleteDialog();
    expect(await slicerOptionDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Slicer Option?');
    await slicerOptionDeleteDialog.clickOnConfirmButton();

    expect(await slicerOptionComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
