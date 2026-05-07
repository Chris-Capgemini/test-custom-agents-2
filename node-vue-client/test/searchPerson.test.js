'use strict';

const { test } = require('node:test');
const assert = require('node:assert/strict');

// The search logic extracted from Search.vue's searchPerson method
const searchSpace = [
  { first: 'Hans', name: 'Mayer', dob: '1981-01-08', zip: '95183', ort: 'Trogen', street: 'Isaaer Str.', hausnr: '23', knr: '79423984', zahlungsempfaenger: [] },
  { first: 'Linda', name: 'Reitmayr', dob: '1979-05-12', zip: '92148', ort: 'Hof', street: 'Erst-Reuther-Str.', hausnr: '51', knr: '67485246', zahlungsempfaenger: [] },
  { first: 'Karl', name: 'May', dob: '1964-11-02', zip: '10124', ort: 'Berlin', street: 'Schäferstr.', hausnr: '12', knr: '13725246', zahlungsempfaenger: [] },
  { first: 'Jens', name: 'Mueller', dob: '1999-04-21', zip: '14489', ort: 'Potsdam', street: 'August-Bebel-Str.', hausnr: '79', knr: '41125291', zahlungsempfaenger: [] },
  { first: 'Steffi', name: 'Ruckmueller', dob: '1961-11-05', zip: '14432', ort: 'Templin', street: 'Charlottenstr.', hausnr: '16', knr: '31228193', zahlungsempfaenger: [] }
];

function searchPerson(formdata, searchSpace) {
  const result = [];
  for (const element of searchSpace) {
    if (
      (formdata.last && element.name.toLowerCase().indexOf(formdata.last.toLowerCase()) >= 0) ||
      (formdata.first && element.first.toLowerCase().indexOf(formdata.first.toLowerCase()) >= 0) ||
      (formdata.zip && element.zip === formdata.zip) ||
      (formdata.ort && element.ort.toLowerCase().indexOf(formdata.ort.toLowerCase()) >= 0) ||
      (formdata.street && element.street.toLowerCase().indexOf(formdata.street.toLowerCase()) >= 0) ||
      (formdata.hausnr && element.hausnr.toLowerCase().indexOf(formdata.hausnr.toLowerCase()) >= 0)
    ) {
      result.push(element);
    }
  }
  return result;
}

test('searchPerson finds by last name (case-insensitive)', () => {
  const results = searchPerson({ last: 'mayer' }, searchSpace);
  assert.equal(results.length, 1);
  assert.equal(results[0].name, 'Mayer');
});

test('searchPerson finds by first name', () => {
  const results = searchPerson({ first: 'Hans' }, searchSpace);
  assert.equal(results.length, 1);
  assert.equal(results[0].first, 'Hans');
});

test('searchPerson finds by zip code', () => {
  const results = searchPerson({ zip: '14489' }, searchSpace);
  assert.equal(results.length, 1);
  assert.equal(results[0].name, 'Mueller');
});

test('searchPerson finds by city (Ort)', () => {
  const results = searchPerson({ ort: 'berlin' }, searchSpace);
  assert.equal(results.length, 1);
  assert.equal(results[0].name, 'May');
});

test('searchPerson finds by street name', () => {
  const results = searchPerson({ street: 'charlotten' }, searchSpace);
  assert.equal(results.length, 1);
  assert.equal(results[0].name, 'Ruckmueller');
});

test('searchPerson finds partial name match (Mueller matches Mueller and Ruckmueller)', () => {
  const results = searchPerson({ last: 'ueller' }, searchSpace);
  assert.equal(results.length, 2);
});

test('searchPerson returns empty array when nothing matches', () => {
  const results = searchPerson({ last: 'Nonexistent' }, searchSpace);
  assert.equal(results.length, 0);
});

test('searchPerson returns empty array with empty formdata', () => {
  const results = searchPerson({}, searchSpace);
  assert.equal(results.length, 0);
});
