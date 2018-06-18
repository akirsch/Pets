/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.Data.PetContract;
import com.example.android.pets.Data.PetContract.PetEntry;
import com.example.android.pets.Data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>{

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    private final int EXISTING_PET_LOADER_ID = 0;



    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetEntry.GENDER_UNKNOWN;

    // declare variable to store uri provided by intent which opens the Activity
    Uri currentPetUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = findViewById(R.id.edit_pet_name);
        mBreedEditText = findViewById(R.id.edit_pet_breed);
        mWeightEditText = findViewById(R.id.edit_pet_weight);
        mGenderSpinner = findViewById(R.id.spinner_gender);

        setupSpinner();

        // get intent object used to open this activity
        Intent intent = getIntent();

        currentPetUri = intent.getData();

        if (currentPetUri != null){
            getSupportActionBar().setTitle(getString(R.string.editor_activity_title_edit_pet));
        } else {
            getSupportActionBar().setTitle(getString(R.string.editor_activity_title_new_pet));
        }

        // create instance of Loader Manager object
        getLoaderManager().initLoader(EXISTING_PET_LOADER_ID, null, this );

    }


    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetEntry.GENDER_UNKNOWN; // Unknown
            }
        });
    }

    public void insertPet() {
        // grab data entered into the editText views and selected from the spinner
        String petNameString = mNameEditText.getText().toString().trim();
        String petBreedString = mBreedEditText.getText().toString().trim();
        int petGender = mGender;
        int petWeight;
        String petWeightString = mWeightEditText.getText().toString().trim();
        if (petWeightString.isEmpty()){
            petWeight = 0;
        } else {
            petWeight = Integer.parseInt(petWeightString);
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, petNameString);
        values.put(PetEntry.COLUMN_PET_BREED, petBreedString);
        values.put(PetEntry.COLUMN_PET_GENDER, petGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, petWeight);

        // Insert the new row, returning the primary key value of the new row
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        String successfulEntryString = getString(R.string.successful_insertion);
        String entryErrorString = getString(R.string.entry_error_string);

        // Display toast message displaying Id of newly added row, or error message if insert is unsuccessful
        if (newUri != null){
            Toast toast = Toast.makeText(getApplicationContext(), successfulEntryString,
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), entryErrorString, Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertPet();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        /*
         * Takes action based on the ID of the Loader that's being created
         */
        switch (loaderID) {
            case EXISTING_PET_LOADER_ID:
                // Since the editor shows all pet attributes, define a projection that contains
                // all columns from the pet table
                String[] projection = {
                        PetEntry._ID,
                        PetEntry.COLUMN_PET_NAME,
                        PetEntry.COLUMN_PET_BREED,
                        PetEntry.COLUMN_PET_GENDER,
                        PetEntry.COLUMN_PET_WEIGHT};

                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(getApplicationContext(),
                        currentPetUri,
                        projection,
                        null,
                        null,
                        null );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        cursor.moveToFirst();

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
        int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
        int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

        // get the values for the current pet in each of the columns
        String currentPetName = cursor.getString(nameColumnIndex);
        String currentPetBreed = cursor.getString(breedColumnIndex);
        int currentPetGender = cursor.getInt(genderColumnIndex);
        int currentPetWeight = cursor.getInt(weightColumnIndex);

        // set these values in the data entry fields in the Activity
        // set the pet name
        mNameEditText.setText(currentPetName);

        // set the pet breed
        mBreedEditText.setText(currentPetBreed);

        // set the pet gender
        if (currentPetGender == PetEntry.GENDER_MALE) {
            mGenderSpinner.setSelection(1);
        } else if (currentPetGender == PetEntry.GENDER_FEMALE){
            mGenderSpinner.setSelection(2);
        } else if (currentPetGender == PetEntry.GENDER_UNKNOWN) {
            mGenderSpinner.setSelection(0);
        }

        // set the pet weight
        mWeightEditText.setText(Integer.toString(currentPetWeight));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // clear all the input entry fields in the activity

        mNameEditText.setText("");
        mBreedEditText.setText("");
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText("");

    }
}