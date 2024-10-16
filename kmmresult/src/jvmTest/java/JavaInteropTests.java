import at.asitplus.KmmResult;
import kotlin.NotImplementedError;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;


public class JavaInteropTests {

    @Test
    public void testJavaInterop() {
        Boolean rnd = demonstrate().getOrNull();
        Assert.assertNotEquals(false, rnd);

    }

    KmmResult<Boolean> demonstrate() {
        if (new Random().nextBoolean())
            return KmmResult.failure(new NotImplementedError("Not Implemented"));
        else
            return KmmResult.success(true);
    }
}
