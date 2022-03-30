/* 
 * Copyright 2015, The Thymeleaf Project (http://www.thymeleaf.org/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function() {
	'use strict';

	// Run the Prism syntax highlighter
	Prism.plugins.NormalizeWhitespace.setDefaults({
		'tabs-to-spaces': 2
	});
	Prism.highlightAll();

	// Have the site menu button reveal the site menu on click
	$('#site-menu-button').addEventListener('click', function(event) {
		$('#site-menu').classList.toggle('show-menu');
	});

})();
