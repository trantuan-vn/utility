// @ts-check
const { test, expect, firefox } = require('@playwright/test');

test('get all stock from price board', async ({page}) => {
  await page.goto('https://fireant.vn/');
});
