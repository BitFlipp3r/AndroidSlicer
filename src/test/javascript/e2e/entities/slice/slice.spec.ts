/* tslint:disable no-unused-expression */
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import { SliceComponentsPage, SliceDeleteDialog, SliceUpdatePage } from './slice.page-object';

const expect = chai.expect;

describe('Slice e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let sliceUpdatePage: SliceUpdatePage;
  let sliceComponentsPage: SliceComponentsPage;
  let sliceDeleteDialog: SliceDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing('admin', 'admin');
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Slice', async () => {
    await navBarPage.goToEntity('slice');
    sliceComponentsPage = new SliceComponentsPage();
    await browser.wait(ec.visibilityOf(sliceComponentsPage.title), 5000);
    expect(await sliceComponentsPage.getTitle()).to.eq('Slice');
  });

  it('should load create Slice page', async () => {
    await sliceComponentsPage.clickOnCreateButton();
    sliceUpdatePage = new SliceUpdatePage();
    expect(await sliceUpdatePage.getPageTitle()).to.eq('Create or edit a Slice');
    await sliceUpdatePage.cancel();
  });

  it('should create and save Slice', async () => {
    const nbButtonsBeforeCreate = await sliceComponentsPage.countDeleteButtons();

    await sliceComponentsPage.clickOnCreateButton();
    await promise.all([
      sliceUpdatePage.setAndroidVersionInput('5'),
      sliceUpdatePage.setAndroidClassNameInput('androidClassName'),
      sliceUpdatePage.setEntryMethodsInput('entryMethods'),
      sliceUpdatePage.setSeedStatementsInput('seedStatements'),
      sliceUpdatePage.setSliceInput('slice'),
      sliceUpdatePage.setLogInput('log'),
      sliceUpdatePage.setThreadIdInput('threadId'),
      sliceUpdatePage.reflectionOptionsSelectLastOption(),
      sliceUpdatePage.dataDependenceOptionsSelectLastOption(),
      sliceUpdatePage.controlDependenceOptionsSelectLastOption()
    ]);
    expect(await sliceUpdatePage.getAndroidVersionInput()).to.eq('5', 'Expected androidVersion value to be equals to 5');
    expect(await sliceUpdatePage.getAndroidClassNameInput()).to.eq(
      'androidClassName',
      'Expected AndroidClassName value to be equals to androidClassName'
    );
    expect(await sliceUpdatePage.getEntryMethodsInput()).to.eq('entryMethods', 'Expected EntryMethods value to be equals to entryMethods');
    expect(await sliceUpdatePage.getSeedStatementsInput()).to.eq(
      'seedStatements',
      'Expected SeedStatements value to be equals to seedStatements'
    );
    expect(await sliceUpdatePage.getSliceInput()).to.eq('slice', 'Expected Slice value to be equals to slice');
    expect(await sliceUpdatePage.getLogInput()).to.eq('log', 'Expected Log value to be equals to log');
    expect(await sliceUpdatePage.getThreadIdInput()).to.eq('threadId', 'Expected ThreadId value to be equals to threadId');
    const selectedRunning = sliceUpdatePage.getRunningInput();
    if (await selectedRunning.isSelected()) {
      await sliceUpdatePage.getRunningInput().click();
      expect(await sliceUpdatePage.getRunningInput().isSelected(), 'Expected running not to be selected').to.be.false;
    } else {
      await sliceUpdatePage.getRunningInput().click();
      expect(await sliceUpdatePage.getRunningInput().isSelected(), 'Expected running to be selected').to.be.true;
    }
    await sliceUpdatePage.save();
    expect(await sliceUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await sliceComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Slice', async () => {
    const nbButtonsBeforeDelete = await sliceComponentsPage.countDeleteButtons();
    await sliceComponentsPage.clickOnLastDeleteButton();

    sliceDeleteDialog = new SliceDeleteDialog();
    expect(await sliceDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Slice?');
    await sliceDeleteDialog.clickOnConfirmButton();

    expect(await sliceComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
