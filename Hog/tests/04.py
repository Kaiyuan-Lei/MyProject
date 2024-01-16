test = {
  'name': 'Question 4',
  'points': 1,
  'suites': [
    {
      'cases': [
        {
          'code': r"""
          >>> is_swap(19, 91)
          False
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(19, 19)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(123, 12)
          False
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(1, 2)
          False
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(12, 2)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(2, 2)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(36, 35)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(114, 12)
          True
          >>> is_swap(108, 7)
          False
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(18, 108)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(8, 108)
          True
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(20, 0)
          False
          """,
          'hidden': False,
          'locked': False
        },
        {
          'code': r"""
          >>> is_swap(10, 16)
          True
          """,
          'hidden': False,
          'locked': False
        }
      ],
      'scored': True,
      'setup': r"""
      >>> from hog import *
      """,
      'teardown': '',
      'type': 'doctest'
    }
  ]
}
