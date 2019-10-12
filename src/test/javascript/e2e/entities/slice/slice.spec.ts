// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { SliceComponentsPage, SliceDeleteDialog, SliceMakePage } from './slice.page-object';
import * as path from 'path';

const expect = chai.expect;

describe('Slice e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let sliceComponentsPage: SliceComponentsPage;
  let sliceMakePage: SliceMakePage;
  let sliceDeleteDialog: SliceDeleteDialog;
  const fileNameToUpload = 'logo-jhipster.png';
  const fileToUpload = '../../../../../../src/main/webapp/content/images/' + fileNameToUpload;
  const absolutePath = path.resolve(__dirname, fileToUpload);

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
    sliceMakePage = new SliceMakePage();
    expect(await sliceMakePage.getPageTitle()).to.eq('Create or edit a Slice');
    await sliceMakePage.cancel();
  });

  it('should create and save Slice', async () => {
    const nbButtonsBeforeCreate = await sliceComponentsPage.countDeleteButtons();

    await sliceComponentsPage.clickOnCreateButton();
    await promise.all([
      sliceMakePage.setAndroidVersionInput('5'),
      sliceMakePage.setAndroidClassNameInput('androidClassName'),
      sliceMakePage.setEntryMethodsInput('entryMethods'),
      sliceMakePage.setSeedStatementsInput('seedStatements'),
      sliceMakePage.cfaTypeSelectLastOption(),
      sliceMakePage.setCfaLevelInput('5'),
      sliceMakePage.reflectionOptionsSelectLastOption(),
      sliceMakePage.dataDependenceOptionsSelectLastOption(),
      sliceMakePage.controlDependenceOptionsSelectLastOption()
    ]);
    expect(await sliceMakePage.getAndroidVersionInput()).to.eq('5', 'Expected androidVersion value to be equals to 5');
    expect(await sliceMakePage.getAndroidClassNameInput()).to.eq(
      'androidClassName',
      'Expected AndroidClassName value to be equals to androidClassName'
    );
    expect(await sliceMakePage.getEntryMethodsInput()).to.eq('entryMethods', 'Expected EntryMethods value to be equals to entryMethods');
    expect(await sliceMakePage.getSeedStatementsInput()).to.eq(
      'seedStatements',
      'Expected SeedStatements value to be equals to seedStatements'
    );
    expect(await sliceMakePage.getCfaLevelInput()).to.eq('5', 'Expected cfaLevel value to be equals to 5');
    await sliceMakePage.save();
    expect(await sliceMakePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

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
