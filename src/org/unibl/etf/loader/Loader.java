package org.unibl.etf.loader;

import java.util.*;
import org.unibl.etf.instruction.*;

public interface Loader {
	public List<Instruction> loadInstructions();
}