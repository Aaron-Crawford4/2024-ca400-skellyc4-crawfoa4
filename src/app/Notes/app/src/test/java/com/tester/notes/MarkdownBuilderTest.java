package com.tester.notes;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.tester.notes.activities.CreateNoteActivity;
import com.tester.notes.utils.markdownBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.image.ImagesPlugin;

@RunWith(RobolectricTestRunner.class)
public class MarkdownBuilderTest {
    CreateNoteActivity activity;
    Markwon markwon;
    @Before
    public void setUp(){
        try (ActivityController<CreateNoteActivity> controller = Robolectric.buildActivity(CreateNoteActivity.class)){
            controller.setup();
            activity = controller.get();
        }
        markwon = markdownBuilder.getMarkwon(activity.getApplicationContext());
        assertNotNull(markwon);
    }
    @Test
    public void hasStrikethroughPlugin(){
        assertTrue(markwon.hasPlugin(StrikethroughPlugin.class));
    }
    @Test
    public void hasImagesPlugin(){
        assertTrue(markwon.hasPlugin(ImagesPlugin.class));
    }
    @Test
    public void hasTablePlugin(){
        assertTrue(markwon.hasPlugin(TablePlugin.class));
    }
    @Test
    public void hasTaskListPlugin(){
        assertTrue(markwon.hasPlugin(TaskListPlugin.class));
    }
}
